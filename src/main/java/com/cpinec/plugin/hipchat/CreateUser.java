package com.cpinec.plugin.hipchat;

import com.atlassian.jira.util.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/9/29.
 */
public class CreateUser extends ApiRequest {
    public static final Logger log = LoggerFactory.getLogger(CreateUser.class);

    private String name;
    private String title;
    private String mentionName;
    private boolean isGroupAdmin = false;
    private String timezone = "UTC";
    private String password;
    private String email;
    private HttpPost post;

    public CreateUser(String accessToken, String baseUrl) {
        super(accessToken, baseUrl);
    }

    public String getName() {
        return name;
    }

    public CreateUser setName(String name) {
        this.name = name;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public CreateUser setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getMentionName() {
        return mentionName;
    }

    public CreateUser setMentionName(String mentionName) {
        this.mentionName = mentionName;
        return this;
    }

    public boolean getIsGroupAdmin() {
        return isGroupAdmin;
    }

    public CreateUser setIsGroupAdmin(boolean isGroupAdmin) {
        this.isGroupAdmin = isGroupAdmin;
        return this;
    }

    public String getTimezone() {
        return timezone;
    }

    public CreateUser setTimezone(String timezone) {
        this.timezone = timezone;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public CreateUser setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public CreateUser setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getRestUrl() {
        return "v2/user";
    }

    public CreateUser buildRequest(HttpClient client) throws IOException {

        // Generate the request and url
        String url = getBaseUrl() + getRestUrl();
        post = new HttpPost(url);

        //  Map out the request
        Map req = new HashMap<String, Object>();
        try {
            req.put("name", getName());
            if (title != null) { req.put("title", getTitle()); }
            if (mentionName != null) { req.put("mention_name", getMentionName()); }
            req.put("is_group_admin", getIsGroupAdmin());
            req.put("timezone", getTimezone());
            if (password != null) { req.put("password", getPassword()); }
            req.put("email", getEmail());
        } catch (NullPointerException e) {
            log.error("Not all values have been defined");
        }
        // Convert request data into JSON string for the POST request
        JSONObject requestData = new JSONObject(req);
        StringEntity input = new StringEntity(requestData.toString());

        // Set up the request
        post.setEntity(input);
        authorize(post);

        return this;
    }

    public void sendRequest(HttpClient client) throws ClientProtocolException, IOException {

        // Execute the request
        HttpResponse response = client.execute(post);

        // Print out the result
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line = "";
        while ((line = rd.readLine()) != null) {
            log.info(line);
        }
    }
}
