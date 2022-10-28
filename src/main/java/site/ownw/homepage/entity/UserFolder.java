package site.ownw.homepage.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserFolder extends AbstractEntity {

    private String name;

    private String path;

    private Long userId;

    // if null is in root folder
    private Long parentFolderId;
}
