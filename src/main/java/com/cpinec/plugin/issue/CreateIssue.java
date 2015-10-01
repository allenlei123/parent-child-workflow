package com.cpinec.plugin.issue;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.util.ErrorCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Administrator on 2015/9/29.
 */
public class CreateIssue {
    private static final Logger log = LoggerFactory.getLogger(CreateIssue.class);

    private IssueService issueService;
    private JiraAuthenticationContext jAC;

    private Long projectId;
    private String assigneeId;
    private String summary;
    private String reporterId;
    private String issueTypeId;
    private String description;
    private String parentIssueId;
    private Map<String, Object> extraData;

    private IssueInputParameters issueIP;

    public CreateIssue(IssueService issueService, JiraAuthenticationContext jAC) {
        this.issueService = issueService;
        this.jAC = jAC;
        issueIP = issueService.newIssueInputParameters();

    }

    public Long getProjectId() {
        return projectId;
    }

    public CreateIssue setProjectId(Long projectId) {
        this.projectId = projectId;
        return this;
    }

    public String getAssigneeId() {
        return assigneeId;
    }

    public CreateIssue setAssigneeId(String assigneeId) {
        this.assigneeId = assigneeId;
        return this;
    }

    public String getSummary() {
        return summary;
    }

    public CreateIssue setSummary(String summary) {
        this.summary = summary;
        return this;
    }

    public String getReporterId() {
        return reporterId;
    }

    public CreateIssue setReporterId(String reporterId) {
        this.reporterId = reporterId;
        return this;
    }

    public String getIssueTypeId() {
        return issueTypeId;
    }

    public CreateIssue setIssueTypeId(String issueTypeId) {
        this.issueTypeId = issueTypeId;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public CreateIssue setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getParentIssueId() {
        return parentIssueId;
    }

    public CreateIssue setParentIssueId(String parentIssueId) {
        this.parentIssueId = parentIssueId;
        return this;
    }

    public Map<String, Object> getExtraData() {
        return extraData;
    }

    public CreateIssue setExtraData(Map<String, Object> extraData) {
        this.extraData = extraData;
        return this;
    }

    public IssueInputParameters getIssueIP() {
        return issueIP;
    }

    public CreateIssue build() {
        try {
            issueIP.setProjectId(projectId);
            issueIP.setAssigneeId(assigneeId);
            issueIP.setSummary(summary);
            issueIP.setReporterId(reporterId);
            issueIP.setIssueTypeId(issueTypeId);
            issueIP.setDescription(description);

        } catch (NullPointerException e) {
            log.error("Missing value for Create Issue\n" + e.getStackTrace().toString());
        }
        return this;
    }

    public Issue create() {
        IssueInputParameters issueIp = getIssueIP();
        IssueService.CreateValidationResult issue;
        issue = issueService.validateCreate(jAC.getUser(), issueIP);
        if (issue.isValid()) {
            ErrorCollection errors = issue.getErrorCollection();
            if (errors.hasAnyErrors()) {
                log.error("Error in creating issue");
            } else {
                issueService.create(jAC.getUser(), issue);
            }
        } else {
            ErrorCollection errors = issue.getErrorCollection();
            Collection<String> errorMessages = errors.getErrorMessages();
            for (String message : errorMessages) {
                log.error("Error: " + message);
            }
            Map<String, String> errorMap = issue.getErrorCollection().getErrors();
            log.error(errorMap.size() + " size of errorMap");
            for (String key : errorMap.keySet()) {
                log.error("Error: " + key);
            }
            log.error("Could not create issue");
        }
        return issue.getIssue();
    }
}
