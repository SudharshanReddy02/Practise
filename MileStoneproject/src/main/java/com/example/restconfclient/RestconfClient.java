package com.example.restconfclient;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

public class RestconfClient {
    private static final String BASE_URL = "http://192.168.1.1/restconf/data";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "password";

    private final HttpClient httpClient;

    public RestconfClient() {
        this.httpClient = HttpClient.newHttpClient();
    }

    // Helper method for authentication
    private String getBasicAuthHeader() {
        String auth = USERNAME + ":" + PASSWORD;
        return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
    }

    // GET Request: Retrieve Current Configuration
    public String getNetworkConfig() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(BASE_URL + "/network-config"))
                .header("Authorization", getBasicAuthHeader())
                .header("Accept", "application/yang-data+json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new RuntimeException("GET request failed: " + response.statusCode());
        }
    }

    // POST Request: Add VLAN
    public String addVlan(int vlanId, String vlanName) throws Exception {
        String jsonPayload = "{ \"vlan-id\": " + vlanId + ", \"name\": \"" + vlanName + "\" }";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(BASE_URL + "/network-config/vlans"))
                .header("Authorization", getBasicAuthHeader())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 201) {
            return "VLAN " + vlanId + " added successfully.";
        } else {
            throw new RuntimeException("POST request failed: " + response.statusCode());
        }
    }

    // DELETE Request: Delete VLAN
    public String deleteVlan(int vlanId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(BASE_URL + "/network-config/vlans/" + vlanId))
                .header("Authorization", getBasicAuthHeader())
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 204) {
            return "VLAN " + vlanId + " deleted successfully.";
        } else {
            throw new RuntimeException("DELETE request failed: " + response.statusCode());
        }
    }

    // Main method for testing
    public static void main(String[] args) {
        try {
            RestconfClient client = new RestconfClient();
            
            // GET Request
            System.out.println("Retrieving current network configuration...");
            String config = client.getNetworkConfig();
            System.out.println("Configuration retrieved successfully: " + config);

            // POST Request
            System.out.println("\nAdding VLAN 100...");
            String addResponse = client.addVlan(100, "VLAN100");
            System.out.println(addResponse);

            // DELETE Request
            System.out.println("\nDeleting VLAN 50...");
            String deleteResponse = client.deleteVlan(50);
            System.out.println(deleteResponse);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
