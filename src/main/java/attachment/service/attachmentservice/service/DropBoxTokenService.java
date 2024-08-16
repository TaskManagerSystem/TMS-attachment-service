package attachment.service.attachmentservice.service;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.DbxClientV2;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DropBoxTokenService {

    private DbxRequestConfig config;
    @Value("${dropbox.appKey}")
    private String dropboxAppKey;
    @Value("${dropbox.appSecret}")
    private String dropBoxAppSecret;
    @Value("${dropbox.refreshToken}")
    private String dropboxRefreshToken;

    @PostConstruct
    private void init() {
        config = DbxRequestConfig.newBuilder("AttachmentService").build();
    }

    public DbxClientV2 getClient() {
        return refreshClient();
    }

    private DbxClientV2 refreshClient() {
        DbxCredential credential = new DbxCredential("",
                -1L,
                dropboxRefreshToken,
                dropboxAppKey,
                dropBoxAppSecret);
        try {
            return new DbxClientV2(config, credential.refresh(new DbxRequestConfig(dropboxAppKey))
                    .getAccessToken());
        } catch (DbxException e) {
            throw new RuntimeException("Can not refresh dropBox client");
        }
    }
}
