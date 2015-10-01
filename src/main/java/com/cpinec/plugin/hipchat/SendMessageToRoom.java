package com.cpinec.plugin.hipchat;

import com.atlassian.jira.util.json.JSONObject;
import org.apache.http.HttpResponse;
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
public class SendMessageToRoom extends ApiRequest {
    public static final Logger log = LoggerFactory.getLogger(CreateRoom.class);

    private String roomName;
    private String color = "yellow";
    private String message;
    private boolean notify = false;
    private String messageFormat = "html";
    private HttpPost post;

    public SendMessageToRoom(String accessToken, String baseUrl, String roomName) {
        super(accessToken, baseUrl);
        setRoomName(roomName);
    }

    public String getRoomName() {
        return roomName;
    }

    public SendMessageToRoom setRoomName(String roomName) {
        this.roomName = roomName;
        return this;
    }

    public String getColor() {
        return color;
    }

    public SendMessageToRoom setColor(String color) {
        this.color = color;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public SendMessageToRoom setMessage(String message) {
        this.message = message;
        return this;
    }

    public boolean getNotify() {
        return notify;
    }

    public SendMessageToRoom setNotify(boolean notify) {
        this.notify = notify;
        return this;
    }

    public String getMessageFormat() {
        return messageFormat;
    }

    public SendMessageToRoom setMessageFormat(String messageFormat) {
        this.messageFormat = messageFormat;
        return this;
    }

    public String getRestUrl() {
        String restUrl = "v2/room/" + getRoomName() + "/notification";
        return restUrl;
    }

    public SendMessageToRoom buildRequest(HttpClient client) throws IOException {

        // Generate the request and url
        String url = getBaseUrl() + getRestUrl();
        post = new HttpPost(url);

        Map req = new HashMap<String, Object>();

        //  Map out the request
        try {
            req.put("color", getColor());
            req.put("message", getMessage());
            req.put("notify", getNotify());
            req.put("message_format", getMessageFormat());
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

    public void sendRequest(HttpClient client) throws IOException {

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
