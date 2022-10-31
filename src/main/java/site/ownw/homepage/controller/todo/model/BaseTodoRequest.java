package site.ownw.homepage.controller.todo.model;

import io.swagger.v3.oas.annotations.Parameter;
import java.time.OffsetDateTime;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BaseTodoRequest {

    @Parameter(required = true)
    @NotBlank
    private String content;

    @Parameter(required = false)
    private OffsetDateTime remindDateTime;
}
