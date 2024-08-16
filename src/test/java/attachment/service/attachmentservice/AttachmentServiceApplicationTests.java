package attachment.service.attachmentservice;

import attachment.service.attachmentservice.service.DropBoxTokenService;
import com.dropbox.core.v2.DbxClientV2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class AttachmentServiceApplicationTests {

    @MockBean
    private DropBoxTokenService dropBoxTokenService;

    @BeforeEach
    public void setUp() {
        DbxClientV2 mockClient = Mockito.mock(DbxClientV2.class);
        Mockito.when(dropBoxTokenService.getClient()).thenReturn(mockClient);
    }

    @Test
    void contextLoads() {
    }
}
