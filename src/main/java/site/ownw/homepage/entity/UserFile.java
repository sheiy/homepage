package site.ownw.homepage.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserFile extends AbstractEntity {

    private Long folderId;

    private String name;

    private String path;

    private Long size;

    private String contentType;

    private Long userId;
}
