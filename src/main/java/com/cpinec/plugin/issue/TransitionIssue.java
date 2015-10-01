package com.cpinec.plugin.issue;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.ErrorCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Administrator on 2015/9/29.
 */
public class TransitionIssue {
    private static final Logger log = LoggerFactory.getLogger(TransitionIssue.class);

    private IssueService issueService;
    private JiraAuthenticationContext jAC;

    private Long issueId;
    private int actionId;
    private Long projectId;
    private String assigneeId;
    private String summary;
    private String reporterId;
    private String issueTypeId;
    private String description;
    private IssueInputParameters issueIP;

    public TransitionIssue(IssueService issueService, JiraAuthenticationContext jAC) {
        this.issueService = issueService;
        this.jAC = jAC;
        issueIP = issueService.newIssueInputParameters();
    }

    public Long getIssueId() {
        return issueId;
    }

    public TransitionIssue setIssueId(Long issueId) {
        this.issueId = issueId;
        return this;
    }

    public Long getProjectId() {
        return projectId;
    }

    public TransitionIssue setProjectId(Long projectId) {
        this.projectId = projectId;
        return this;
    }

    public String getAssigneeId() {
        return assigneeId;
    }

    public TransitionIssue setAssigneeId(String assigneeId) {
        this.assigneeId = assigneeId;
        return this;
    }

    public String getSummary() {
        return summary;
    }

    public TransitionIssue setSummary(String summary) {
        this.summary = summary;
        return this;
    }

    public String getReporterId() {
        return reporterId;
    }

    public TransitionIssue setReporterId(String reporterId) {
        this.reporterId = reporterId;
        return this;
    }

    public String getIssueTypeId() {
        return issueTypeId;
    }

    public TransitionIssue setIssueTypeId(String issueTypeId) {
        this.issueTypeId = issueTypeId;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public TransitionIssue setDescription(String description) {
        this.description = description;
        return this;
    }

    public int getActionId() {
        return actionId;
    }

    public TransitionIssue setActionId(int actionId) {
        this.actionId = actionId;
        return this;
    }

    public IssueInputParameters getIssueIP() {
        return issueIP;
    }

    public TransitionIssue build() {
        try {

        } catch (NullPointerException e) {
            log.error("Missing values for TransitionIssue\n" + e.getStackTrace().toString());
        }
        return this;
    }

    public void transition() {
        IssueInputParameters issueIp = getIssueIP();
        ApplicationUser user = jAC.getUser();
        Long issueId = getIssueId();
        int actionId = getActionId();
        IssueService.TransitionValidationResult issue;

        try {
//            issueIP.setProjectId(projectId);
            issueIP.setAssigneeId(assigneeId);
//            issueIP.setSummary(summary);
//            issueIP.setReporterId(reporterId);
//            issueIP.setIssueTypeId(issueTypeId);
//            issueIP.setDescription(description);

        } catch (NullPointerException e) {
            log.error("Missing value for Create Issue\n" + e.getStackTrace().toString());
        }

        issue = issueService.validateTransition(user, issueId, actionId, issueIp);
        if (issue.isValid()) {
            IssueService.IssueResult transitionResult = issueService.transition(user, issue);
            if (!transitionResult.isValid()) {
                log.error("Transition result is not valid");
            }
        } else {
            ErrorCollection errors = issue.getErrorCollection();
            Collection<String> errorMessages = errors.getErrorMessages();
            for (String message : errorMessages) {
                log.error("Error: " + message);
            }
            Map<String, String> errorMap = issue.getErrorCollection().getErrors();
            for (String key : errorMap.keySet()) {
                log.error("Error: " + key);
            }
            log.error("Could not transition issue");
        }
        issue.getIssue();
    }
}
