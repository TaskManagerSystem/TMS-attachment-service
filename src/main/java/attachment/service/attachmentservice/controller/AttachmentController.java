package attachment.service.attachmentservice.controller;

import attachment.service.attachmentservice.service.AttachmentService;
import attachment.service.attachmentservice.service.TokenValidation;
import com.dropbox.core.DbxException;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/attachments")
@RequiredArgsConstructor
public class AttachmentController {
    private final TokenValidation tokenValidation;
    private final AttachmentService attachmentService;

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("taskId") Long taskId,
                                             @RequestParam("file") MultipartFile file,
                                             @RequestHeader("Authorization") String token) {
        tokenValidation.sendTokenToValidate(token);

        boolean isValid = tokenValidation.waitForTokenValidation(token);

        if (isValid) {
            try {
                String dropboxFileId = attachmentService.save(taskId, file);

                log.info("File uploaded successfully for taskId: {}. Dropbox File ID: {}",
                        taskId, dropboxFileId);

                return ResponseEntity.ok("File uploaded successfully. "
                        + "Dropbox File ID: " + dropboxFileId);
            } catch (IOException | DbxException e) {
                log.error("Failed to upload file for taskId: {}. Error: {}",
                        taskId, e.getMessage(), e);

                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to upload file: " + e.getMessage());
            }
        } else {
            log.warn("Invalid token received for file upload request for taskId: {}", taskId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @GetMapping
    public ResponseEntity<String> downloadFile(@RequestParam Long taskId,
                                               @RequestHeader("Authorization") String token) {
        tokenValidation.sendTokenToValidate(token);

        boolean isValid = tokenValidation.waitForTokenValidation(token);

        if (isValid) {
            try {
                String link = attachmentService.downloadFromDropBox(taskId);

                log.info("File downloaded successfully for taskId: {}", taskId);

                return ResponseEntity.ok(link);
            } catch (DbxException e) {
                log.error("Failed to download the file for taskId: {}. Error: {}",
                        taskId, e.getMessage(), e);

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to download the file. Please try again later.");
            } catch (EntityNotFoundException e) {
                log.warn("Attachment not found for taskId: {}", taskId);

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Attachment not found for the given task ID.");
            }
        } else {
            log.warn("Invalid token received for download request for taskId: {}", taskId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id,
                                             @RequestHeader("Authorization") String token) {
        log.info("Received request to delete attachment with id: {}", id);

        tokenValidation.sendTokenToValidate(token);

        boolean isValid = tokenValidation.waitForTokenValidation(token);
        if (isValid) {
            try {
                attachmentService.deleteAttachment(id);

                log.info("Attachment with id: {} was deleted successfully.", id);

                return ResponseEntity.status(HttpStatus.ACCEPTED)
                        .body("Attachment dy id: " + id + "was deleted");
            } catch (EntityNotFoundException | DbxException e) {
                log.error("Failed to delete attachment with id: {}. Error: {}",
                        id, e.getMessage(), e);

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Attachment not found for the given task ID: " + id);
            }
        } else {
            log.warn("Invalid token received for delete request for attachment id: {}", id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }
}
