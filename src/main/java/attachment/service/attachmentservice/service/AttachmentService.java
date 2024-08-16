package attachment.service.attachmentservice.service;

import com.dropbox.core.DbxException;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService {
    String save(Long taskId, MultipartFile file) throws IOException, DbxException;

    String downloadFromDropBox(Long id) throws DbxException;

    void deleteAttachment(Long id) throws DbxException;
}
