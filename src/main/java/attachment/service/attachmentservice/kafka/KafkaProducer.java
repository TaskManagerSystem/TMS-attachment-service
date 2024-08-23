package attachment.service.attachmentservice.kafka;

import com.example.dto.IsVerificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendTokenToValidate(IsVerificationDto dto) {
        log.info("Token is sending to validate: " + dto.getToken());
        ProducerRecord<String, Object> record = new ProducerRecord<>("token-validation-topic",
                dto);
        log.info("Record sending: " + record);
        kafkaTemplate.send(record);
    }
}
