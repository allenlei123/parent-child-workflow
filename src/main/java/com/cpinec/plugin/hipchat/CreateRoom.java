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
public class CreateRoom extends ApiRequest{
    public static final Logger log = LoggerFactory.getLogger(CreateRoom.class);

    private String topic;
    private boolean guestAccess;
    private String roomName;
    private String ownerUserId;
    private String privacy;
    private HttpPost post;

    public CreateRoom(String accessToken, String baseUrl) {
        super(accessToken, baseUrl);
    }

    public String getTopic() {
        return topic;
    }

    public CreateRoom setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public boolean getGuestAccess() {
        return guestAccess;
    }

    public CreateRoom setGuestAccess(boolean guestAccess) {
        this.guestAccess = guestAccess;
        return this;
    }

    public String getRoomName() {
        return roomName;
    }

    public CreateRoom setRoomName(String roomName) {
        this.roomName = roomName;
        return this;
    }

    public String getOwnerUserId() {
        return ownerUserId;
    }

    public CreateRoom setOwnerUserId(String ownerUserId) {
        this.ownerUserId = ownerUserId;
        return this;
    }

    public String getPrivacy() {
        return privacy;
    }

    public CreateRoom setPrivacy(String privacy) {
        this.privacy = privacy;
        return this;
    }

    public String getRestUrl() {
        return "v2/room";
    }

    public CreateRoom buildRequest(HttpClient client) throws IOException {

        // Generate the request and url
        String url = getBaseUrl() + getRestUrl();
        post = new HttpPost(url);

        //  Map out the request
        Map req = new HashMap<String, Object>();
        try {
            req.put("topic", getTopic());
            req.put("guest_access", getGuestAccess());
            req.put("name", getRoomName());
            req.put("owner_user_id", getOwnerUserId());
            req.put("privacy", getPrivacy());
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

        int code = response.getStatusLine().getStatusCode();

//        if (code == 500){
//            throw new IOException("code is 500....................................................");
//        }

        // Print out the result
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line = "";
        while ((line = rd.readLine()) != null) {
            log.info(line);
        }
    }
}
