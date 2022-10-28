package site.ownw.homepage.domain.file.model;

import lombok.Data;
import site.ownw.homepage.common.enums.FileType;

@Data
public class GetFilesItem {

    private FileType type;
    private String name;
    private Long id;
    private Long size;
    private String contentType;
}
