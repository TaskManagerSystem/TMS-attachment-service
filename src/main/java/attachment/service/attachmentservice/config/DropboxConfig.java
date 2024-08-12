package attachment.service.attachmentservice.config;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DropboxConfig {
    private DbxRequestConfig config;

    @Value("${dropbox.accessToken}")
    private String accessToken;
    @Value("${dropbox.refreshToken}")
    private String refreshToken;

    @Value("${dropbox.appKey}")
    private String appKey;

    @Value("${dropbox.appSecret}")
    private String appSecret;

    private Instant tokenExpiryTime = Instant.now().plusSeconds(7200);

    @Bean
    public DbxClientV2 dbxClientV2() {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("AttachmentService").build();

        if (Instant.now().isAfter(tokenExpiryTime.minusSeconds(300))) {
            refreshAccessToken();
        }
        return new DbxClientV2(config, accessToken);
    }

    private void refreshAccessToken() {
        try {
            String url = "https://api.dropboxapi.com/oauth2/token";
            Map<String, String> parameters = new HashMap<>();
            parameters.put("grant_type", "refresh_token");
            parameters.put("refresh_token", refreshToken);
            parameters.put("client_id", appKey);
            parameters.put("client_secret", appSecret);

            String response = sendResponse(url, parameters);

            ObjectMapper mapper = new ObjectMapper();

            JsonNode jsonResponse = mapper.readTree(response);

            accessToken = jsonResponse.get("access_token").asText();
            int expiration = jsonResponse.get("expires_in").asInt();
            tokenExpiryTime = Instant.now().plusSeconds(expiration);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String sendResponse(String url, Map<String, String> parameters) throws IOException {
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            if (!postData.isEmpty()) {
                postData.append("&");
            }
            postData.append(entry.getKey()).append('=').append(entry.getValue());
        }

        URL urlObj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setDoOutput(true);

        byte[] postDateBytes = postData.toString().getBytes(StandardCharsets.UTF_8);
        connection.getOutputStream().write(postDateBytes);

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            return new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } else {
            throw new IOException("Http error code: " + connection.getResponseCode());
        }
    }
}
