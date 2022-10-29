package site.ownw.homepage.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Bookmark extends AbstractEntity {

    private String name;

    private String url;

    private String cleanUrl;

    private Long bookmarkGroupId;

    private Integer sort;

    private Long userId;

    private String favicon;
}
