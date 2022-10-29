package site.ownw.homepage.controller.bookmark.model;

import io.swagger.v3.oas.annotations.Parameter;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class UpdateBookmarkRequest {

    @Parameter(required = true)
    @NotBlank
    private String name;

    @Parameter(required = true)
    @URL
    private String url;
}
