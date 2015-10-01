package com.cpinec.plugin.utils;

import com.atlassian.plugin.util.ClassLoaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Administrator on 2015/9/29.
 */
public class PropertiesLoader {
    public static final Logger log = LoggerFactory.getLogger(PropertiesLoader.class);
    private Properties properties;

    public PropertiesLoader() {
        try {
            properties = new Properties();
            loadProperties();
        } catch (IOException e) {
            log.error("Cannot instantiate properties file");
        }

    }

    public void loadProperties() throws IOException{

        InputStream input = null;
        try {
            input = ClassLoaderUtils.getResourceAsStream("parent-child-workflow.properties", this.getClass());
            log.info("this is the input:" + input.toString());
            properties.load(input);
        } catch (IOException e) {
            log.error("Could not load properties file");
        }
        finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    log.error(e.getStackTrace().toString());
                }
            }
        }

    }

    public String getProp(String key) {
        return properties.getProperty(key);
    }

    public boolean getBoolProp(String key) { return Boolean.valueOf(properties.getProperty(key)); }
}
