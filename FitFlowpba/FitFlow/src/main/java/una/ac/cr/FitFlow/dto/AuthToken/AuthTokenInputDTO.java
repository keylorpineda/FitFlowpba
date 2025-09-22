package una.ac.cr.FitFlow.dto.AuthToken;

import jakarta.validation.constraints.*;
import java.time.OffsetDateTime;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthTokenInputDTO {

    @NotBlank
    private String token;                

    @NotNull
    private OffsetDateTime expiresAt;  

    @NotNull @Positive
    private Long userId;
}
