package com.cpinec.plugin.entrance;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;
import com.cpinec.plugin.hipchat.HipChatClient;
import com.cpinec.plugin.issue.IssueClient;
import com.cpinec.plugin.project.ProjectClient;
import com.cpinec.plugin.utils.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/9/28.
 */
public class EventHandler implements InitializingBean, DisposableBean{
    private static final Logger log = LoggerFactory.getLogger(EventHandler.class);

    private final EventPublisher eventPublisher;
    private static List<Map<String, String>> model;
    private static HipChatClient hipChatClient = new HipChatClient();
    private static IssueClient issueClient = new IssueClient();
    private static ProjectClient projectClient = new ProjectClient();

    /**
     * Constructor.
     * @param eventPublisher injected {@code EventPublisher} implementation.
     */
    public EventHandler(EventPublisher eventPublisher) {
        System.out.println("##################################################");
        this.eventPublisher = eventPublisher;
    }

    /**
     * Called when the plugin has been enabled.
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception{
        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        // register ourselves with the EventPublisher
        eventPublisher.register(this);

        model = Parser.createModel();
    }

    /**
     * Called when the plugin is being disabled or removed.
     * @throws Exception
     */
    @Override
    public void destroy() throws Exception {
        // unregister ourselves with the EventPublisher
        eventPublisher.unregister(this);
    }

    @EventListener
    public void onIssueEvent(IssueEvent issueEvent) throws Exception{
        Long eventTypeId = issueEvent.getEventTypeId();
        Issue issue = issueEvent.getIssue();

        if (eventTypeId.equals(EventType.ISSUE_CREATED_ID)) {
            onIssueCreate(issue);
            log.info("Issue {} has been ++++created at {}.", issue.getKey(), issue.getCreated());
        } else if (eventTypeId.equals(EventType.ISSUE_RESOLVED_ID)) {
            log.info("Issue {} has been ****resolved at {}.", issue.getKey(), issue.getResolutionDate());
        } else if (eventTypeId.equals(EventType.ISSUE_CLOSED_ID)) {
            onIssueClose(issue);
            log.info("Issue {} has been -----closed at {}.", issue.getKey(), issue.getUpdated());
        }
    }

    public static void onIssueCreate(Issue issue) throws Exception{
        // Check for proper event
        for (Map<String, String> event : model) {
            String projectName = event.get("project");
            if (projectName.equals(issue.getProjectObject().getName())) {
                String issueEventType = event.get("issueEvent");
                if (issueEventType.equals("on-create-issue")) {
                    doEventAction(event, issue);
                } else {
                    continue;
                }
            } else {
                continue;
            }
        }
    }

    public static void onIssueClose(Issue issue) throws Exception{

        // Check for proper event
        for (Map<String, String> event : model) {
            String projectName = event.get("project");
            if (projectName.equals(issue.getProjectObject().getName())) {
                String issueEventType = event.get("issueEvent");
                if (issueEventType.equals("on-close-issue")) {
                    doEventAction(event, issue);
                } else {
                    continue;
                }
            } else {
                continue;
            }
        }
    }

    /**
     * key parentId initialized here, which in turn being set for "Parent Issue Id"
     * @param event
     * @param issue
     */
    public static void doEventAction(Map<String, String> event, Issue issue) throws Exception{
        String action = event.get("eventAction");
        if (action.equals("create-issue")) {
            event.put("parentId", issue.getId().toString()); // put current issue id to parentId, which will be referred by child node
            createIssue(event);
        } else if (action.equals("close-issue")) {
            event.put("parentId",issue.getId().toString());
            closeIssue(event, issue);
        } else if (action.equals("create-chatroom")) {
            createChatRoom(event);
        } else if (action.equals("close-chatroom")) {
            closeChatRoom(event);
        } else {
            log.error("Not a valid event action: " + action);
        }
    }

    /**
     * Custom Field "Parent Issue ID" initialized here
     * @param data
     */
    public static void createIssue(Map<String, String> data) {
        System.out.println("--------------------------------" + data.get("projectName") + ", parentId: " + data.get("parentId"));
        issueClient.authenticate("admin");
        Issue issue = issueClient.createIssue(projectClient.getProjectIdWithName(data.get("projectName")),
                "admin",
                "My summary",
                "admin",
                "3",
                "my description");

        // Set custom value with value of parent id for this newly created child issue
        issueClient.saveValue((MutableIssue) issue, data.get("parentId"), issueClient.getCustomField("Parent Issue ID"));
    }

    public static void closeIssue(Map<String, String> data, Issue issue) throws Exception{
        Long parentIssueId = Long.parseLong((String) issue.getCustomFieldValue(issueClient.getCustomField("Parent Issue ID")));
        Issue parentIssue = issueClient.getIssue(parentIssueId);
        issueClient.transitionIssue(parentIssue, parentIssueId, issueClient.getActionId(parentIssue, data.get("closeActionName")));
    }

    public static void createChatRoom(Map<String, String> data) {
        hipChatClient.createRoom(data.get("parentId") + data.get("prefix"));
    }

    public static void closeChatRoom(Map<String, String> data) {
        hipChatClient.deleteRoom(data.get("parentId") + data.get("prefix"));
    }

}
