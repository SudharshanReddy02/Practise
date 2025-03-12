package com.network;

import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.ContentType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Base64;

public class RestconfClient {

    private static final String BASE_URL = "http://192.168.1.1/restconf/data/network-config";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "password";

    private static String getAuthHeader() {
        String auth = USERNAME + ":" + PASSWORD;
        return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
    }

    public static void getCurrentConfig() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(BASE_URL);
            request.addHeader("Authorization", getAuthHeader());

            try (CloseableHttpResponse response = client.execute(request)) {
                if (response.getCode() == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode jsonNode = mapper.readTree(result.toString());
                    System.out.println("Current Configuration: " + jsonNode.toPrettyString());
                } else {
                    System.out.println("Failed to retrieve configuration. Status Code: " + response.getCode());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addVLAN(int vlanId, String vlanName) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(BASE_URL + "/vlans");
            request.addHeader("Authorization", getAuthHeader());
            request.addHeader("Content-Type", "application/json");

            String json = "{ \"vlan-id\": " + vlanId + ", \"name\": \"" + vlanName + "\" }";
            request.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = client.execute(request)) {
                if (response.getCode() == 201 || response.getCode() == 200) {
                    System.out.println("VLAN " + vlanId + " added successfully.");
                } else {
                    System.out.println("Failed to add VLAN. Status Code: " + response.getCode());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteVLAN(int vlanId) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpDelete request = new HttpDelete(BASE_URL + "/vlans/" + vlanId);
            request.addHeader("Authorization", getAuthHeader());

            try (CloseableHttpResponse response = client.execute(request)) {
                if (response.getCode() == 204 || response.getCode() == 200) {
                    System.out.println("VLAN " + vlanId + " deleted successfully.");
                } else {
                    System.out.println("Failed to delete VLAN. Status Code: " + response.getCode());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("Retrieving current network configuration...");
        getCurrentConfig();

        System.out.println("\nAdding VLAN 100...");
        addVLAN(100, "VLAN100");

        System.out.println("\nDeleting VLAN 50...");
        deleteVLAN(50);
    }
}