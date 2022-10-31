package site.ownw.homepage.controller.user.model;

import io.swagger.v3.oas.annotations.Parameter;
import java.util.Map;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public abstract class BaseUserConfigRequest {

    @Parameter(required = true)
    @NotEmpty
    private Map<String, Object> value;
}
