package site.ownw.homepage.controller.file.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
public class BatchDeleteFileResponse {

    @ArraySchema(schema = @Schema(required = true, implementation = String.class))
    private List<Long> successful;

    @ArraySchema(schema = @Schema(required = true, implementation = String.class))
    private List<Long> failed;
}
