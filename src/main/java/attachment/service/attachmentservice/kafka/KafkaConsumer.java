package attachment.service.attachmentservice.kafka;

import attachment.service.attachmentservice.service.TokenValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaConsumer {
    private final TokenValidation tokenValidation;

    @KafkaListener(topics = "token-validation-response-topic", groupId = "task-manager-systems")
    public void receiveValidationResponse(String response) {
        tokenValidation.handleValidationResponse(response);
    }
}
