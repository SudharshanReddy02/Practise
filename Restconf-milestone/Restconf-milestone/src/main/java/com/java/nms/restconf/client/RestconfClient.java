package com.java.nms.restconf.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class RestconfClient {
	

	    private static final String DEVICE_IP = "172.20.0.88";
	    private static final String USERNAME = "admin";
	    private static final String PASSWORD = "cisco123";

	    private static String getBasicAuthHeader() {
	        String auth = USERNAME + ":" + PASSWORD;
	        return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
	    }

	    public static void main(String[] args) {
	        try {
	            retrieveConfig();
	            addVlan(100, "VLAN100");
	            deleteVlan(50);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    public static void retrieveConfig() throws IOException {
	        System.out.println("Retrieving current network configuration...");
	        URL url = new URL("http://" + DEVICE_IP + "/restconf/data/network-config");
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setRequestMethod("GET");
	        connection.setRequestProperty("Authorization", getBasicAuthHeader());
	        connection.setRequestProperty("Accept", "application/json");

	        int responseCode = connection.getResponseCode();
	        if (responseCode == HttpURLConnection.HTTP_OK) {
	            System.out.println("Configuration retrieved successfully.");
	            String response = readResponse(connection);
	            System.out.println(response);
	        } else {
	            System.err.println("GET request failed: " + responseCode);
	            String errorResponse = readResponse(connection);
	            System.err.println(errorResponse);
	        }
	        connection.disconnect();
	    }

	    public static void addVlan(int vlanId, String vlanName) throws IOException {
	        System.out.println("Adding VLAN " + vlanId + "...");
	        URL url = new URL("http://" + DEVICE_IP + "/restconf/data/network-config/vlans");
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setRequestMethod("POST");
	        connection.setRequestProperty("Authorization", getBasicAuthHeader());
	        connection.setRequestProperty("Content-Type", "application/json");
	        connection.setRequestProperty("Accept", "application/json");
	        connection.setDoOutput(true);

	        String jsonInputString = "{\"vlan-id\": " + vlanId + ", \"name\": \"" + vlanName + "\"}";

	        try (OutputStream os = connection.getOutputStream()) {
	            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
	            os.write(input, 0, input.length);
	        }

	        int responseCode = connection.getResponseCode();
	        if (responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_OK) {
	            System.out.println("VLAN " + vlanId + " added successfully.");
	            String response = readResponse(connection);
	            System.out.println(response);
	        } else {
	            System.err.println("POST request failed: " + responseCode);
	            String errorResponse = readResponse(connection);
	            System.err.println(errorResponse);
	        }
	        connection.disconnect();
	    }

	    public static void deleteVlan(int vlanId) throws IOException {
	        System.out.println("Deleting VLAN " + vlanId + "...");
	        URL url = new URL("http://" + DEVICE_IP + "/restconf/data/network-config/vlans/vlan-id=" + vlanId);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setRequestMethod("DELETE");
	        connection.setRequestProperty("Authorization", getBasicAuthHeader());
	        connection.setRequestProperty("Accept", "application/json");

	        int responseCode = connection.getResponseCode();
	        if (responseCode == HttpURLConnection.HTTP_NO_CONTENT || responseCode == HttpURLConnection.HTTP_OK) {
	            System.out.println("VLAN " + vlanId + " deleted successfully.");
	        } else {
	            System.err.println("DELETE request failed: " + responseCode);
	            String errorResponse = readResponse(connection);
	            System.err.println(errorResponse);
	        }
	        connection.disconnect();
	    }

	    private static String readResponse(HttpURLConnection connection) throws IOException {
	        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
	            StringBuilder response = new StringBuilder();
	            String responseLine;
	            while ((responseLine = br.readLine()) != null) {
	                response.append(responseLine.trim());
	            }
	            return response.toString();
	        } catch (IOException e) {
	            try(BufferedReader errBr = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))){
	                StringBuilder errorResponse = new StringBuilder();
	                String errorLine;
	                while((errorLine = errBr.readLine()) != null){
	                    errorResponse.append(errorLine.trim());
	                }
	                return errorResponse.toString();
	            }
	            catch(IOException errE){
	                return "Could not read response";
	            }
	        }
	    }
	}
	

