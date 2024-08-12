package attachment.service.attachmentservice.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class AttachmentDto {
    private Long taskId;
    private String fileName;
    private LocalDateTime uploadTime;
    private String dropBoxFile;

}
