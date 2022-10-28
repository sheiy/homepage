package site.ownw.homepage.controller.bookmark.model;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SortBookmarkRequest {

    @Parameter(required = true)
    @ArraySchema(schema = @Schema(implementation = String.class))
    @NotEmpty
    private List<Long> bookmarkIds;
}
