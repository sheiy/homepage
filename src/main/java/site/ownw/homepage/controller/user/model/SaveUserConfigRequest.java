package site.ownw.homepage.controller.user.model;

import io.swagger.v3.oas.annotations.Parameter;
import java.util.Map;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SaveUserConfigRequest {

    @Parameter(required = true)
    @NotEmpty
    private Map<String, Object> value;
}
