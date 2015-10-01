package com.cpinec.plugin.hipchat;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;

import java.io.IOException;

/**
 * Created by Administrator on 2015/9/29.
 */
public abstract class ApiRequest {
    private String accessToken;
    private String baseUrl;

    public ApiRequest(String accessToken, String baseUrl) {
        this.accessToken = accessToken;
        this.baseUrl = baseUrl;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    protected void authorize(HttpDelete req) {
        req.setHeader("content-type", "application/json");
        req.addHeader("Authorization", "Bearer " + getAccessToken());
    }

    protected void authorize(HttpGet req) {
        req.setHeader("content-type", "application/json");
        req.addHeader("Authorization", "Bearer " + getAccessToken());
    }

    protected void authorize(HttpPost req) {
        req.setHeader("content-type", "application/json");
        req.addHeader("Authorization", "Bearer " + getAccessToken());
    }

    protected void authorize(HttpPut req) {
        req.setHeader("content-type", "application/json");
        req.addHeader("Authorization", "Bearer " + getAccessToken());
    }

    public abstract ApiRequest buildRequest(HttpClient client) throws IOException;

    public abstract void sendRequest(HttpClient client) throws IOException;

    public void fireRequest(HttpClient client) throws IOException{
        buildRequest(client);
        sendRequest(client);
    }
}
