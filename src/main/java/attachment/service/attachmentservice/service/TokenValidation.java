package attachment.service.attachmentservice.service;

import com.example.dto.IsVerificationDto;
import java.util.concurrent.CompletableFuture;

public interface TokenValidation {
    void sendTokenToValidate(IsVerificationDto dto);

    void handleValidationResponse(IsVerificationDto response);

    boolean waitForTokenValidation(String token);

    void subscribeToTokenValidationResponse(String token, CompletableFuture<Boolean> future);
}
