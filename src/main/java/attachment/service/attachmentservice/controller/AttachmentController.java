package attachment.service.attachmentservice.controller;

import attachment.service.attachmentservice.service.AttachmentService;
import com.dropbox.core.DbxException;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/attachments")
@RequiredArgsConstructor
public class AttachmentController {
    private final AttachmentService attachmentService;

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("taskId") Long taskId,
                                             @RequestParam("file") MultipartFile file) {
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

    @GetMapping
    public ResponseEntity<String> downloadFile(@RequestParam Long taskId) {
        try {
            String link = attachmentService.downloadFromDropBox(taskId);
            return ResponseEntity.ok(link);
        } catch (DbxException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to download the file. Please try again later.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Attachment not found for the given task ID.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id) {
        try {
            attachmentService.deleteAttachment(id);
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body("Attachment dy id: " + id + "was deleted");
        } catch (EntityNotFoundException | DbxException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Attachment not found for the given task ID: " + id);
        }
    }
}