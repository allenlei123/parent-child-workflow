package com.cpinec.plugin.project;

import com.atlassian.jira.bc.project.ProjectService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.cpinec.plugin.utils.PropertiesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2015/9/29.
 */
public class ProjectClient {
    private static Logger log = LoggerFactory.getLogger(ProjectClient.class);

    private PropertiesLoader properties;
    private ProjectService projectService;
    private ProjectManager projectManager;

    public ProjectClient() {
        properties = new PropertiesLoader();
        projectManager = ComponentAccessor.getProjectManager();
    }

    // Basic client functions

    public Project getProjectWithName(String name) {
        Project project = projectManager.getProjectObjByName(name);
        return project;
    }

    public String getProjectIdWithName(String name) {
        Project project = projectManager.getProjectObjByName(name);
        return project.getId().toString();
    }
}
