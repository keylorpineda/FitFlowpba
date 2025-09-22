package una.ac.cr.FitFlow.dto.AuthToken;

import java.time.OffsetDateTime;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LoginTokenDTO {
    private String token;
    private OffsetDateTime expiresAt;
    private Long userId;
}
