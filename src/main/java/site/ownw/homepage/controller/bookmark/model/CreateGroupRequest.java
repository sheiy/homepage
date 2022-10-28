package site.ownw.homepage.controller.bookmark.model;

import io.swagger.v3.oas.annotations.Parameter;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateGroupRequest {

    @Parameter(required = true)
    @NotBlank
    private String name;

    @Parameter(required = true)
    @NotNull
    private Integer sort;
}
