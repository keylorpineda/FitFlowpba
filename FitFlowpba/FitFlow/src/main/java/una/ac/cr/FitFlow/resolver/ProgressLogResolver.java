// ProgressLogResolver.java
package una.ac.cr.FitFlow.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.graphql.data.method.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.List;

import una.ac.cr.FitFlow.dto.ProgressLog.ProgressLogInputDTO;
import una.ac.cr.FitFlow.dto.ProgressLog.ProgressLogOutputDTO;
import una.ac.cr.FitFlow.model.Role;
import una.ac.cr.FitFlow.security.SecurityUtils;
import una.ac.cr.FitFlow.service.ProgressLog.ProgressLogService;

@Controller
@RequiredArgsConstructor
public class ProgressLogResolver {

  private static final Role.Module MODULE = Role.Module.PROGRESO;

  private final ProgressLogService service;

  @QueryMapping
  public ProgressLogOutputDTO progressLogById(@Argument Long id) {
    SecurityUtils.requireRead(MODULE);
    return service.findById(id);
  }

  @QueryMapping
  public Object progressLogs(@Argument int page, @Argument int size) {
    SecurityUtils.requireRead(MODULE);
    Pageable pageable = PageRequest.of(page, size);
    Page<ProgressLogOutputDTO> p = service.list(pageable);
    return pageDTO(p);
  }

  @QueryMapping
  public Object progressLogsByUser(@Argument Long userId, @Argument int page, @Argument int size) {
    SecurityUtils.requireRead(MODULE);
    Pageable pageable = PageRequest.of(page, size);
    Page<ProgressLogOutputDTO> p = service.listByUser(userId, pageable);
    return pageDTO(p);
  }

  @QueryMapping
  public List<ProgressLogOutputDTO> progressLogsByUserOnDate(@Argument Long userId,
                                                             @Argument OffsetDateTime date) {
    SecurityUtils.requireRead(MODULE);
    return service.listByUserOnDate(userId, date);
  }

  @MutationMapping
  public ProgressLogOutputDTO createProgressLog(@Argument("input") ProgressLogInputDTO input) {
    SecurityUtils.requireWrite(MODULE);
    return service.create(input);
  }

  @MutationMapping
  public ProgressLogOutputDTO updateProgressLog(@Argument Long id, @Argument("input") ProgressLogInputDTO input) {
    SecurityUtils.requireWrite(MODULE);
    return service.update(id, input);
  }

  @MutationMapping
  public Boolean deleteProgressLog(@Argument Long id) {
    SecurityUtils.requireWrite(MODULE);
    service.delete(id);
    return true;
  }

  private Object pageDTO(Page<ProgressLogOutputDTO> p) {
    return new java.util.HashMap<>() {{
      put("content", p.getContent());
      put("totalElements", p.getTotalElements());
      put("totalPages", p.getTotalPages());
      put("pageNumber", p.getNumber());
      put("pageSize", p.getSize());
    }};
  }
}
