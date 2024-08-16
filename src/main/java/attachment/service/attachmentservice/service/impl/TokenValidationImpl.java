package attachment.service.attachmentservice.service.impl;

import attachment.service.attachmentservice.kafka.KafkaProducer;
import attachment.service.attachmentservice.service.TokenValidation;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenValidationImpl implements TokenValidation {
    private final KafkaProducer producer;

    private final Map<String, CompletableFuture<Boolean>> responseMap = new ConcurrentHashMap<>();

    @Override
    public void sendTokenToValidate(String token) {
        producer.sendTokenToValidate(token);
    }

    @Override
    public void handleValidationResponse(String response) {
        log.info("Got response: " + response);
        try {
            String[] parts = response.split(":");

            String token = parts[0];
            boolean isValid = Boolean.parseBoolean(parts[1]);

            CompletableFuture<Boolean> future = responseMap.remove(token);

            if (future != null) {
                future.complete(isValid);
                log.info("Token " + token + " validation result: " + isValid);
            }
        } catch (Exception e) {
            log.error("Error handling validation response: " + response, e);
        }
    }

    public boolean waitForTokenValidation(String token) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        subscribeToTokenValidationResponse(token, future);

        try {
            return future.get();
        } catch (Exception e) {
            return false;
        }
    }

    public void subscribeToTokenValidationResponse(String token,
                                                   CompletableFuture<Boolean> future) {
        responseMap.put(token, future);
    }
}
