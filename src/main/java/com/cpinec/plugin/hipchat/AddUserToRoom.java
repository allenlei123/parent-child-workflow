package com.cpinec.plugin.hipchat;

import com.atlassian.jira.util.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
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
public class AddUserToRoom extends ApiRequest {
    public static final Logger log = LoggerFactory.getLogger(AddUserToRoom.class);

    private String userId;
    private String roomName;
    private HttpPut put;

    public AddUserToRoom(String accessToken, String baseUrl) {
        super(accessToken, baseUrl);
    }

    public String getUserId() {
        return userId;
    }

    public AddUserToRoom setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getRoomName() {
        return roomName;
    }

    public AddUserToRoom setRoomName(String roomName) {
        this.roomName = roomName;
        return this;
    }

    public String getRestUrl() {
        String url = "v2/room/" + getRoomName() + "/member/" + getUserId();
        return url;
    }

    public AddUserToRoom buildRequest(HttpClient client) throws IOException {

        // Generate the request and url
        String url;
        try {
            url = getBaseUrl() + getRestUrl();
            put = new HttpPut(url);
        } catch (NullPointerException e) {
            log.error("Cannot generate AddUserToRoom url because of undefined values \n" + e.getStackTrace().toString());
        }

        //  Map out the request
        Map req = new HashMap<String, Object>();
        try {

        } catch (NullPointerException e) {
            log.error("Not all values have been defined");
        }
        // Convert request data into JSON string for the POST request
        JSONObject requestData = new JSONObject(req);
        StringEntity input = new StringEntity(requestData.toString());

        // Set up the request
        put.setEntity(input);
        authorize(put);

        return this;
    }

    public void sendRequest(HttpClient client) throws ClientProtocolException, IOException {

        // Execute the request
        HttpResponse response = client.execute(put);

        // Print out the result
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line = "";
        while ((line = rd.readLine()) != null) {
            log.info(line);
        }
    }
}
