package attachment.service.attachmentservice.controller;

import attachment.service.attachmentservice.dto.AttachmentDTO;
import attachment.service.attachmentservice.service.AttachmentService;
import com.dropbox.core.DbxException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/attachments")
@RequiredArgsConstructor
public class Controller {
    private final AttachmentService attachmentService;

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("taskId") Long taskId,
                                             @RequestParam("file")MultipartFile file) {
        try {
            String dropboxFileId = attachmentService.uploadFile(taskId, file);
            return ResponseEntity.ok("File uploaded successfully. "
                    + "Dropbox File ID: " + dropboxFileId);
        } catch (IOException | DbxException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload file: " + e.getMessage());
        }
    }
}
