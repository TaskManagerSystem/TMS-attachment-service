package attachment.service.attachmentservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendTokenToValidate(String token) {
        log.info("Token is sending to validate: " + token);
        ProducerRecord<String, String> record = new ProducerRecord<>("token-validation-topic",
                token);
        log.info("Record sending: " + record);
        kafkaTemplate.send(record);
    }
}
