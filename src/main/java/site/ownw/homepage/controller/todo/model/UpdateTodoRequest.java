package site.ownw.homepage.controller.todo.model;

import io.swagger.v3.oas.annotations.Parameter;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import site.ownw.homepage.common.enums.TodoStatus;

@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateTodoRequest extends BaseTodoRequest {

    @Parameter(required = true)
    @NotNull
    private TodoStatus status;
}
