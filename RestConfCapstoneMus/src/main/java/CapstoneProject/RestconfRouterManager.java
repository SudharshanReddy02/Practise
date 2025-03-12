package CapstoneProject;

import javax.net.ssl.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.cert.X509Certificate;
import java.security.NoSuchAlgorithmException;
import java.security.KeyManagementException;
import java.util.Base64;

public class RestconfRouterManager {

    private static final String BASE_URL = "https://172.20.0.73/restconf/data/Cisco-IOS-XE-native:native";
    private static final String VLAN_URL = BASE_URL + "/vlan";
    private static final String AUTH = Base64.getEncoder().encodeToString("admin:cisco123".getBytes());

    public static void main(String[] args) {
        HttpClient client = createHttpClient();
        retrieveConfig(client);
    }

    // ✅ Create HTTP Client with Disabled SSL & Hostname Verification
    private static HttpClient createHttpClient() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) {}

                public void checkServerTrusted(X509Certificate[] chain, String authType) {}

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new java.security.SecureRandom());

            // Disable hostname verification
            SSLParameters sslParams = new SSLParameters();
            sslParams.setEndpointIdentificationAlgorithm("");  // This disables hostname verification

            return HttpClient.newBuilder()
                    .sslContext(sslContext)
                    .sslParameters(sslParams)  // Apply disabled hostname verification
                    .build();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException("Error creating SSL context", e);
        }
    }

    // ✅ Retrieve Configuration from RESTCONF
    private static void retrieveConfig(HttpClient client) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .header("Authorization", "Basic " + AUTH)
                    .header("Accept", "application/yang-data+json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Status Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());
        } catch (Exception e) {
            System.out.println("Error retrieving configuration: " + e.getMessage());
        }
    }
}