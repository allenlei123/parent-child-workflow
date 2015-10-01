package com.cpinec.plugin.hipchat;

import com.cpinec.plugin.utils.PropertiesLoader;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by Administrator on 2015/9/29.
 */
public class HipChatClient {
    private static final Logger log = LoggerFactory.getLogger(HipChatClient.class);
    PropertiesLoader properties;
    private String accessToken;
    private String baseUrl;
    private HttpClient client;

    public HipChatClient() {
        properties = new PropertiesLoader();
        this.accessToken = properties.getProp("hipchat.server.accessToken");
        this.baseUrl = properties.getProp("hipchat.server.baseUrl");
        client = HttpClientBuilder.create().build();
    }

    // User API calls

    public void createUser(String name, String email) {
        try {
            CreateUser req = new CreateUser(accessToken, baseUrl);
            req.setName(name)
                    .setEmail(email);
            req.fireRequest(client);

        } catch (Exception e) {
            log.error("Cannot create a new user");
        }

    }

    public void addUserToRoom(String userId, String roomName) {
        try {
            AddUserToRoom req = new AddUserToRoom(accessToken, baseUrl);
            req.setUserId(userId)
                    .setRoomName(roomName);
            req.fireRequest(client);
        } catch (Exception e) {
            log.error("Cannot add user to room: \n" + e.getStackTrace().toString());
        }
    }

    // Room API calls

    public void createRoom(String roomName) {
        try {
            CreateRoom req = new CreateRoom(accessToken, baseUrl);
            req.setTopic("my topic")
                    .setGuestAccess(false)
                    .setRoomName(roomName)
                    .setOwnerUserId(properties.getProp("hipchat.server.rest.roomOwner"))
                    .setPrivacy("private");
            req.fireRequest(client);

        } catch (Exception e) {
            log.error("Cannot create a new room");
        }
    }

    public void deleteRoom(String roomName) {
        try {
            DeleteRoom req = new DeleteRoom(accessToken, baseUrl);
            req.setRoomName(roomName);
            req.fireRequest(client);
        } catch (Exception e) {
            log.error("Cannot delete room");
        }
    }

    public void sendMessageToRoom(String roomName, String message) throws IOException {
        try {
            SendMessageToRoom req = new SendMessageToRoom(accessToken, baseUrl, roomName);
            req.setColor(properties.getProp("hipchat.server.notifications.color"))
                    .setNotify(properties.getBoolProp("hipchat.server.notifications.notify"))
                    .setMessage(message)
                    .setMessageFormat(properties.getProp("hipchat.server.notifications.messageFormat"));
            req.fireRequest(client);

        } catch (Exception e) {
            log.error("Cannot create a new room");
        }
    }
}
