package site.ownw.homepage.domain.bookmark.repository;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import site.ownw.homepage.entity.BookmarkGroup;

public interface BookmarkGroupRepository extends PagingAndSortingRepository<BookmarkGroup, Long> {

    List<BookmarkGroup> findAllByUserId(Long userId, Sort sort);
}
