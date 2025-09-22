package una.ac.cr.FitFlow.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import una.ac.cr.FitFlow.model.Role;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class SecurityUtils {

    private SecurityUtils() {}

    /* =========================
       Básicos del contexto
       ========================= */

    /** Obtiene el Authentication actual; si no hay, lanza 401 */
    public static Authentication currentAuthOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities() == null) {
            throw new AuthenticationCredentialsNotFoundException("Unauthorized");
        }
        return auth;
    }

    /** Email del principal (tu filtro setea el principal como String email) */
    public static String currentEmail() {
        Authentication auth = currentAuthOrThrow();
        Object principal = auth.getPrincipal();
        return principal == null ? null : principal.toString();
    }

    /** Authorities actuales (nombres) */
    public static Set<String> currentAuthorityNames() {
        Authentication auth = currentAuthOrThrow();
        return auth.getAuthorities() == null
                ? Collections.emptySet()
                : auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(HashSet::new));
    }

    /* =========================
       Helpers de nombres
       ========================= */

    /** Convierte Module + Permission a "MODULE_PERMISSION" (p.ej., GUIAS_EDITOR) */
    public static String authority(Role.Module module, Role.Permission permission) {
        return module.name() + "_" + permission.name();
    }

    /* =========================
       Predicados (boolean)
       ========================= */

    public static boolean has(Role.Module module, Role.Permission permission) {
        return currentAuthorityNames().contains(authority(module, permission));
    }

    public static boolean hasAny(Role.Module module, Role.Permission... perms) {
        Set<String> mine = currentAuthorityNames();
        for (Role.Permission p : perms) {
            if (mine.contains(authority(module, p))) return true;
        }
        return false;
    }

    /** Lectura: EDITOR o AUDITOR del módulo */
    public static boolean canRead(Role.Module module) {
        return hasAny(module, Role.Permission.EDITOR, Role.Permission.AUDITOR);
    }

    /** Escritura: solo EDITOR del módulo */
    public static boolean canWrite(Role.Module module) {
        return has(module, Role.Permission.EDITOR);
    }

    /* =========================
       Requerimientos (throw)
       ========================= */

    /** Requiere exactamente MODULE_PERMISSION, si no => 403 */
    public static void require(Role.Module module, Role.Permission permission) {
        if (!has(module, permission)) {
            throw new AccessDeniedException("Forbidden");
        }
    }

    /** Requiere cualquiera de las permissions dadas para ese módulo, si no => 403 */
    public static void requireAny(Role.Module module, Role.Permission... perms) {
        if (!hasAny(module, perms)) {
            throw new AccessDeniedException("Forbidden");
        }
    }

    /** Requiere permiso de lectura en el módulo (EDITOR o AUDITOR), si no => 403 */
    public static void requireRead(Role.Module module) {
        if (!canRead(module)) {
            throw new AccessDeniedException("Forbidden");
        }
    }

    /** Requiere permiso de escritura en el módulo (EDITOR), si no => 403 */
    public static void requireWrite(Role.Module module) {
        if (!canWrite(module)) {
            throw new AccessDeniedException("Forbidden");
        }
    }
}
