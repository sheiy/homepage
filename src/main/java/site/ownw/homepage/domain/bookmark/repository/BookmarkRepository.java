package site.ownw.homepage.domain.bookmark.repository;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import site.ownw.homepage.entity.Bookmark;

public interface BookmarkRepository extends PagingAndSortingRepository<Bookmark, Long> {

    List<Bookmark> findAllByBookmarkGroupId(Long bookmarkGroupId, Sort sort);

    @Modifying
    @Query("delete from `bookmark` where bookmark_group_id = :bookmarkGroupId")
    void deleteAllByBookmarkGroupId(Long bookmarkGroupId);
}
