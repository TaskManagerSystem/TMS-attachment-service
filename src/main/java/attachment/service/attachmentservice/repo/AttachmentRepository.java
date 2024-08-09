package attachment.service.attachmentservice.repo;

import attachment.service.attachmentservice.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
}
