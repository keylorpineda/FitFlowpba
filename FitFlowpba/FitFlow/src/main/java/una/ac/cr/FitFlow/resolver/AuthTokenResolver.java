package una.ac.cr.FitFlow.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.graphql.data.method.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import jakarta.validation.Valid;

import una.ac.cr.FitFlow.dto.AuthToken.AuthTokenPageDTO;
import una.ac.cr.FitFlow.dto.AuthToken.AuthTokenInputDTO;
import una.ac.cr.FitFlow.dto.AuthToken.AuthTokenOutputDTO;
import una.ac.cr.FitFlow.dto.User.UserOutputDTO;
import una.ac.cr.FitFlow.model.Role;
import una.ac.cr.FitFlow.security.SecurityUtils;
import una.ac.cr.FitFlow.service.AuthToken.AuthTokenService;
import una.ac.cr.FitFlow.service.user.UserService;

@RequiredArgsConstructor
@Controller
@Validated
public class AuthTokenResolver {

    private static final Role.Module MODULE = Role.Module.RUTINAS;

    private final AuthTokenService authTokenService;
    private final UserService userService;

    @QueryMapping(name = "authTokenById")
    public AuthTokenOutputDTO authTokenById(@Argument("tokenId") String tokenId) {
        SecurityUtils.requireRead(MODULE);
        return authTokenService.findByToken(tokenId);
    }

    @QueryMapping(name = "authTokensByUserId")
    public AuthTokenPageDTO authTokensByUserId(@Argument("userId") Long userId,
                                               @Argument("page") int page,
                                               @Argument("size") int size) {
        SecurityUtils.requireRead(MODULE);
        Pageable pageable = PageRequest.of(page, size);
        Page<AuthTokenOutputDTO> p = authTokenService.listByUserId(userId, pageable);
        return AuthTokenPageDTO.builder()
                .content(p.getContent())
                .totalElements(p.getTotalElements())
                .totalPages(p.getTotalPages())
                .pageNumber(p.getNumber())
                .pageSize(p.getSize())
                .build();
    }

    @MutationMapping(name = "createAuthToken")
    public AuthTokenOutputDTO createAuthToken(@Valid @Argument("input") AuthTokenInputDTO input) {
        SecurityUtils.requireWrite(MODULE);
        return authTokenService.create(input);
    }

    @MutationMapping(name = "deleteAuthToken")
    public Boolean deleteAuthToken(@Argument("tokenId") String tokenId) {
        SecurityUtils.requireWrite(MODULE);
        if (!authTokenService.isValid(tokenId)) {
            return false;
        }
        authTokenService.delete(tokenId);
        return true;
    }

    @SchemaMapping(typeName = "AuthToken", field = "user")
    public UserOutputDTO user(AuthTokenOutputDTO token) {
        return userService.findUserById(token.getUserId());
    }
}
