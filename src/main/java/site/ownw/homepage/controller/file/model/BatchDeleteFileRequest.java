package site.ownw.homepage.controller.file.model;

import io.swagger.v3.oas.annotations.Parameter;

import java.util.List;
import javax.validation.constraints.NotEmpty;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class BatchDeleteFileRequest {

    @Parameter(required = true)
    @ArraySchema(schema = @Schema(implementation = String.class))
    @NotEmpty
    private List<Long> fileIds;
}
