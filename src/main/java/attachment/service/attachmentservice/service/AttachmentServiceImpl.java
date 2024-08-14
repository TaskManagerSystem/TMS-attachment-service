package attachment.service.attachmentservice.service;

import attachment.service.attachmentservice.entity.Attachment;
import attachment.service.attachmentservice.repo.AttachmentRepository;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.GetTemporaryLinkResult;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {
    private final DbxClientV2 client;
    private final AttachmentRepository attachmentRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public String uploadFile(Long taskId, MultipartFile file) throws IOException, DbxException {
        String dropBoxFileId = uploadFileToDropBox(file);
        Attachment attachment = new Attachment();
        attachment.setTaskId(taskId);
        attachment.setFileName(file.getOriginalFilename());
        attachment.setDropBoxFile(dropBoxFileId);
        attachment.setUploadTime(LocalDateTime.now());
        attachmentRepository.save(attachment);

        kafkaTemplate.send("file-upload", "File- upload with id: " + dropBoxFileId);
        return dropBoxFileId;
    }

    private String uploadFileToDropBox(MultipartFile file) throws IOException, DbxException {
        try (InputStream inputStream = file.getInputStream()) {
            FileMetadata metadata = client.files()
                    .uploadBuilder("/" + file.getOriginalFilename())
                    .uploadAndFinish(inputStream);
            return metadata.getId();
        }
    }

    @Override
    public String downloadFromDropBox(Long id) throws DbxException {
        Attachment attachmentById = findAttachmentById(id);
        String dropBoxFile = attachmentById.getDropBoxFile();
        GetTemporaryLinkResult linkResult = client.files().getTemporaryLink(dropBoxFile);

        kafkaTemplate.send("file-download-link",
                "File could be downloaded from this link: " + linkResult.getLink());
        return linkResult.getLink();
    }

    @Override
    public void deleteAttachment(Long id) throws DbxException {
        Attachment attachmentById = findAttachmentById(id);
        client.files().deleteV2(attachmentById.getDropBoxFile());
        attachmentRepository.deleteById(id);

        kafkaTemplate.send("delete-attachment", "File with id: " + id + " was deleted");
    }

    private Attachment findAttachmentById(Long id) {
        return attachmentRepository
                .findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Can not find attachment by id: " + id));

    }
}
