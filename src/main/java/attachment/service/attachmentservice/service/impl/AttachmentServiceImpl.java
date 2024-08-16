package attachment.service.attachmentservice.service.impl;

import attachment.service.attachmentservice.entity.Attachment;
import attachment.service.attachmentservice.repo.AttachmentRepository;
import attachment.service.attachmentservice.service.AttachmentService;
import attachment.service.attachmentservice.service.DropBoxTokenService;
import com.dropbox.core.DbxException;
import com.dropbox.core.InvalidAccessTokenException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.GetTemporaryLinkResult;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final DropBoxTokenService dropBoxTokenService;
    private DbxClientV2 client;

    @PostConstruct
    private void init() {
        try {
            client = dropBoxTokenService.getClient();
        } catch (Exception e) {
            String message = "Error initializing Dropbox client";
            log.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    @Override
    public String save(Long taskId, MultipartFile file) throws IOException, DbxException {
        String dropBoxFileId = uploadFileToDropBox(file);
        Attachment attachment = new Attachment();
        attachment.setTaskId(taskId);
        attachment.setFileName(file.getOriginalFilename());
        attachment.setDropBoxFile(dropBoxFileId);
        attachment.setUploadTime(LocalDateTime.now());
        attachmentRepository.save(attachment);

        log.info("Attachment created. ID: {}", attachment.getId());

        return dropBoxFileId;
    }

    private String uploadFileToDropBox(MultipartFile file) throws IOException, DbxException {
        try (InputStream inputStream = file.getInputStream()) {
            FileMetadata metadata = client.files()
                    .uploadBuilder("/" + file.getOriginalFilename())
                    .uploadAndFinish(inputStream);
            log.info("File with id: {} uploaded", metadata.getId());
            return metadata.getId();
        } catch (InvalidAccessTokenException e) {
            log.error("Error uploading file to dropBox", e);
            client = refreshClient();
            return uploadFileToDropBox(file);
        }
    }

    @Override
    public String downloadFromDropBox(Long id) throws DbxException {
        try {
            Attachment attachmentById = findAttachmentById(id);
            String dropBoxFile = attachmentById.getDropBoxFile();
            GetTemporaryLinkResult linkResult = client.files().getTemporaryLink(dropBoxFile);
            log.info("Get link for file with id: {}", id);
            return linkResult.getLink();
        } catch (InvalidAccessTokenException e) {
            log.error("Error download file from dropBox", e);
            client = refreshClient();
            return downloadFromDropBox(id);
        }
    }

    @Override
    public void deleteAttachment(Long id) throws DbxException {
        try {
            Attachment attachmentById = findAttachmentById(id);
            client.files().deleteV2(attachmentById.getDropBoxFile());
            log.info("File deleted from dropbox with id: {}", id);
            attachmentRepository.deleteById(id);
        } catch (InvalidAccessTokenException e) {
            log.error("Error delete file from dropBox", e);
            client = refreshClient();
            deleteAttachment(id);
        }
    }

    private Attachment findAttachmentById(Long id) {
        return attachmentRepository
                .findById(id)
                .orElseThrow(() -> {
                    String message = "Can not find attachment by id: " + id;
                    log.error(message);
                    return new EntityNotFoundException(message);
                });

    }

    private DbxClientV2 refreshClient() {
        return dropBoxTokenService.getClient();
    }
}
