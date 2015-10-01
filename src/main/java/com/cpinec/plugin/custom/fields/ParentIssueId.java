package com.cpinec.plugin.custom.fields;

import com.atlassian.jira.issue.customfields.impl.GenericTextCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.fields.TextFieldCharacterLengthValidator;
import com.atlassian.jira.security.JiraAuthenticationContext;

/**
 * Created by Administrator on 2015/9/29.
 */
public class ParentIssueId extends GenericTextCFType {

    public ParentIssueId(CustomFieldValuePersister customFieldValuePersister, GenericConfigManager genericConfigManager,
                         TextFieldCharacterLengthValidator textFieldCharacterLengthValidator, JiraAuthenticationContext jiraAuthenticationContext) {
        super(customFieldValuePersister, genericConfigManager, textFieldCharacterLengthValidator, jiraAuthenticationContext);
    }
}
