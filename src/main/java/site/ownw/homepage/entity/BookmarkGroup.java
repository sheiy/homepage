package site.ownw.homepage.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BookmarkGroup extends AbstractEntity {

    private Long userId;

    private String name;

    private Integer sort;
}
