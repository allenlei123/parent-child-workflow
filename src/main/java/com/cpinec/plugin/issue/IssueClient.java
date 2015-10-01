package com.cpinec.plugin.issue;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.ModifiedValue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.workflow.IssueWorkflowManager;
import com.cpinec.plugin.utils.PropertiesLoader;
import com.opensymphony.workflow.loader.ActionDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/9/29.
 */
public class IssueClient {
    private static final Logger log = LoggerFactory.getLogger(IssueClient.class);

    private PropertiesLoader properties;
    private IssueService issueService;
    private JiraAuthenticationContext jAC;
    private UserManager userManager;


    public IssueClient() {
        properties = new PropertiesLoader();
        issueService = ComponentAccessor.getIssueService();
        jAC = ComponentAccessor.getJiraAuthenticationContext();
        userManager = ComponentAccessor.getUserManager();
    }

    // Authenticate actions
    public void authenticate(String user) {
        jAC.setLoggedInUser(userManager.getUserByName(user));
    }

    public String getCurrentUserId() {
        return jAC.getUser().getKey();
    }

    public int getActionId(Issue issue, String actionName) {
        int result = -1;
        IssueWorkflowManager issueWFMngr = ComponentAccessor.getComponentOfType(IssueWorkflowManager.class);
        Collection<ActionDescriptor> coll = issueWFMngr.getAvailableActions(issue, jAC.getUser());
        for (ActionDescriptor res: coll) {
            if (res.getName().equals(actionName)) {
                result = res.getId();
                break;
            }
        }
        log.info("Action id " + actionName + ": " + Integer.toString(result));
        return result;
    }

    public Issue getIssue(Long id) {
        Issue issue = issueService.getIssue(jAC.getUser(), id).getIssue();
        return issue;
    }

    // Basic built-in issue actions
    public Issue createIssue(String projectId,
                             String assigneeId,
                             String summary,
                             String reporterId,
                             String issueTypeId,
                             String description) {
        CreateIssue createIssueAction = new CreateIssue(issueService, jAC);
        createIssueAction.setProjectId(Long.parseLong(projectId))
                .setAssigneeId(assigneeId)
                .setSummary(summary)
                .setReporterId(reporterId)
                .setIssueTypeId(issueTypeId)
                .setDescription(description);
        createIssueAction.build();
        return createIssueAction.create();
    }

    public void transitionIssue(Issue issue,
                                Long issueId,
                                int actionId) {
        TransitionIssue transitionIssueAction = new TransitionIssue(issueService, jAC);
        transitionIssueAction.setIssueId(issueId)
                .setActionId(actionId)
                .setProjectId(issue.getProjectId())
                .setAssigneeId(issue.getAssigneeId())
                .setSummary(issue.getSummary())
                .setReporterId(issue.getReporterId())
                .setIssueTypeId(issue.getIssueTypeId())
                .setDescription(issue.getDescription())
                .build()
                .transition();
    }

    public void saveValue(MutableIssue issue, String valueToSave, CustomField
            customField) {

        issue.setCustomFieldValue(customField, valueToSave);

        Map<String, ModifiedValue> modifiedFields = issue.getModifiedFields();

        FieldLayoutItem fieldLayoutItem =
                ComponentAccessor.getFieldLayoutManager().getFieldLayout(issue).getFieldLayoutItem(
                        customField);

        DefaultIssueChangeHolder issueChangeHolder = new DefaultIssueChangeHolder();

        final ModifiedValue modifiedValue = (ModifiedValue) modifiedFields.get(customField.getId());

        customField.updateValue(fieldLayoutItem, issue, modifiedValue, issueChangeHolder);
    }

    public CustomField getCustomField(String customFieldName) {
        CustomField customField = ComponentAccessor.getCustomFieldManager().getCustomFieldObjectByName(customFieldName);
        return customField;
    }

    public String getCustomFieldValue(Issue issue, String customFieldName) {
        List<CustomField> customFields = ComponentAccessor.getCustomFieldManager().getCustomFieldObjects();
        for (CustomField cf : customFields) {
            log.info("Custom Field name: " + cf.getFieldName());
        }
        CustomField cfHash = getCustomField(customFieldName);
        String value;
        try {
            value = cfHash.getValueFromIssue(issue);
        } catch (NullPointerException e) {
            log.error("Cannot get custom field value: " + customFieldName);
            value = "";
        }
        return value;

    }

}
