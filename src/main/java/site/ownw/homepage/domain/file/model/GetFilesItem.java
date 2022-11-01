package site.ownw.homepage.domain.file.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import site.ownw.homepage.common.enums.FileType;

@Data
public class GetFilesItem {

    @Schema(required = true)
    private FileType type;

    @Schema(required = true)
    private String name;

    @Schema(required = true, implementation = String.class)
    private Long id;

    @Schema(required = true)
    private Long size;

    @Schema(required = true)
    private String contentType;
}
