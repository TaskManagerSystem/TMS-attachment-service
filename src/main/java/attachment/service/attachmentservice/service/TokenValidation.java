package attachment.service.attachmentservice.service;

import java.util.concurrent.CompletableFuture;

public interface TokenValidation {
    void sendTokenToValidate(String token);

    void handleValidationResponse(String response);

    boolean waitForTokenValidation(String token);

    void subscribeToTokenValidationResponse(String token, CompletableFuture<Boolean> future);
}
