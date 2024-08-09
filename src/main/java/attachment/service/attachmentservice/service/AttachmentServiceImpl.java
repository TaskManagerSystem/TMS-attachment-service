package attachment.service.attachmentservice.service;

import attachment.service.attachmentservice.entity.Attachment;
import attachment.service.attachmentservice.repo.AttachmentRepository;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {
    private final DbxClientV2 client;
    private final AttachmentRepository attachmentRepository;

    @Override
    public String uploadFile(Long taskId, MultipartFile file) throws IOException, DbxException {
        String dropBoxFileId = uploadFileToDropBox(file);
        Attachment attachment = new Attachment();
        attachment.setTaskId(taskId);
        attachment.setFileName(file.getOriginalFilename());
        attachment.setDropBoxFile(dropBoxFileId);
        attachment.setUploadTime(LocalDateTime.now());
        attachmentRepository.save(attachment);
        return dropBoxFileId;
    }

    private String uploadFileToDropBox(MultipartFile file) throws IOException, DbxException {
        try(InputStream inputStream = file.getInputStream()) {
            FileMetadata metadata = client.files()
                    .uploadBuilder("/" + file.getOriginalFilename())
                    .uploadAndFinish(inputStream);
            return metadata.getId();
        }
    }

    @Override
    public void downloadFromDropBox(String fileName) {

    }
}
