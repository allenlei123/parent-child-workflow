package com.cpinec.plugin.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/9/29.
 */
public class Parser {
    public static final Logger log = LoggerFactory.getLogger(Parser.class);

    private static Document doc;

    public static List<Map<String, String>> createModel() {
        updateDoc();
        return buildModel();
    }

    public static void updateDoc()  {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringElementContentWhitespace(true);

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            File file = new File("../../../classes/cross-project-issue-create-close.xml");
            doc = builder.parse(file);
        } catch (ParserConfigurationException e) {
            log.error("ParserConfigurationException in updating doc\n" + e.getStackTrace().toString());
        } catch (SAXException e) {
            log.error("SAXException in updating doc\n" + e.getStackTrace().toString());
        } catch (IOException e) {
            log.error("IOException in updating doc\n" + e.getStackTrace().toString());
            e.printStackTrace();
        } catch (Exception e) {
            log.error("Unable to parse the xml configuration");
        }
    }

    /**
     * for each leaf, there is a list of each step recorded in model.
     * when an issue event comes, it iterates issue's project and its event type, then execute associated children issues creation.
     * @return
     */
    public static List<Map<String, String>> buildModel() {
        List<Map<String, String>> model = new ArrayList<Map<String, String>>();

        // Iterate over the projects
        NodeList projects = doc.getElementsByTagName("project");
        for (int i = 0; i < projects.getLength(); i ++) {
            Node project = projects.item(i);
            if (project.getNodeName().equals("#text")) {
                continue;
            }
            NodeList issueEvents = project.getChildNodes();

            // Iterate over issue actions (on-create-issue and on-close-issue)
            for (int j = 0; j < issueEvents.getLength(); j++) {
                Node issueEvent = issueEvents.item(j);
                if (issueEvent.getNodeName().equals("#text")) {
                    continue;
                }
                NodeList eventActions = issueEvents.item(j).getChildNodes();

                // Iterate over event actions (create-issue, create-chatroom, close-issue, close-chatroom)
                for (int k = 0; k < eventActions.getLength(); k++) {
                    Node eventAction = eventActions.item(k);
                    if (eventAction.getNodeName().equals("#text")) {
                        continue;
                    }
                    NodeList actions = eventAction.getChildNodes();

                    // Iterate over the actions that happen from the events
                    for (int h = 0; h < actions.getLength(); h++) {
                        Node action = actions.item(h);
                        if (action.getNodeName().equals("#text")) {
                            continue;
                        }
                        model.add(parseActionNode(action));
                    }
                }
            }
        }
//        BuildConfiguration.build(model);
        return model;
    }

    private static Map<String, String> parseActionNode(Node action) {
        Map<String, String> model = new HashMap<String, String>();

        // Get the action
        model.put("action", action.getNodeName());

        // Get all attributes
        // Allen: this may be wrong!!!! As for each deepest node, it contains attributes same to other ones
        NamedNodeMap attributes = action.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            String attribute = attributes.item(i).toString();
            String key = attribute.substring(0, attribute.indexOf("="));
            String value = attribute.substring(attribute.indexOf("=") + 1, attribute.length()).replaceAll("\"", "");//drop quota mark
            model.put(key, value);
        }

        // Get the eventAction
        Node eventAction = action.getParentNode();
        model.put("eventAction", eventAction.getNodeName());

        // Get the issueEvent
        Node issueEvent = eventAction.getParentNode();
        model.put("issueEvent", issueEvent.getNodeName());

        // Get the project
        Node project = issueEvent.getParentNode();
        model.put("project", getXMLValue(project, "name"));

        return model;
    }

    private static String getXMLValue(Node namedItem, String attribute) {
        String item = namedItem.getAttributes().getNamedItem(attribute).toString();
        String key = item.substring(attribute.length() + 1, item.length());
        key = key.replaceAll("\"", "");
        return key;
    }
}
