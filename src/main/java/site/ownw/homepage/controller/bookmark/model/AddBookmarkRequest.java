package site.ownw.homepage.controller.bookmark.model;

import io.swagger.v3.oas.annotations.Parameter;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class AddBookmarkRequest {

    @Parameter(required = true)
    @NotBlank
    private String name;

    @Parameter(required = true)
    @URL
    private String url;

    @Parameter(required = true)
    @NotNull
    private Integer sort;
}
