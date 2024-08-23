package attachment.service.attachmentservice.service;

import com.example.dto.IsVerificationDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StringToDtoMapper {

    public IsVerificationDto convertStringToDto(String token) {
        IsVerificationDto dto = new IsVerificationDto();
        dto.setToken(token);
        return dto;
    }
}
