package attachment.service.attachmentservice.service.impl;

import attachment.service.attachmentservice.kafka.KafkaProducer;
import attachment.service.attachmentservice.service.TokenValidation;
import com.example.dto.IsVerificationDto;
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
    public void sendTokenToValidate(IsVerificationDto dto) {
        dto.setValid(false);
        producer.sendTokenToValidate(dto);
    }

    @Override
    public void handleValidationResponse(IsVerificationDto response) {
        log.info("Got response: " + response);
        try {
            String token = response.getToken();
            boolean isValid = response.isValid();

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
            log.error("Error occurred while waiting for token validation for token: {}",
                    token, e);
            return false;
        }
    }

    public void subscribeToTokenValidationResponse(String token,
                                                   CompletableFuture<Boolean> future) {
        log.info("Subscribe to token validation response for token: {}", token);
        responseMap.put(token, future);
    }
}
