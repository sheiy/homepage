package site.ownw.homepage.controller.bookmark.model;

import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SortBookmarkGroupRequest {

    @Parameter(required = true)
    @NotEmpty
    private List<Long> bookmarkGroupIds;
}
