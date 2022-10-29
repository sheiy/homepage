package site.ownw.homepage.controller.bookmark.model;

import io.swagger.v3.oas.annotations.Parameter;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateBookmarkGroupRequest {

    @Parameter(required = true)
    @NotBlank
    private String name;
}
