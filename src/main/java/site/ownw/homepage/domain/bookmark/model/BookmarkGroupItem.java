package site.ownw.homepage.domain.bookmark.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import site.ownw.homepage.entity.Bookmark;

@Data
public class BookmarkGroupItem {

    @Schema(required = true, implementation = String.class)
    private Long id;

    @Schema(required = true, implementation = String.class)
    private String name;

    @Schema(required = true, implementation = String.class)
    private Integer sort;

    private List<Bookmark> bookmarks;
}
