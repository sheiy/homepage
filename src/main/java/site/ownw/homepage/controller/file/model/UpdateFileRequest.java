package site.ownw.homepage.controller.file.model;

import io.swagger.v3.oas.annotations.Parameter;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateFileRequest {

    @Parameter(required = true)
    @NotBlank
    private String name;
}
