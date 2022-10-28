package site.ownw.homepage.domain.bookmark;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import site.ownw.homepage.controller.bookmark.model.AddBookmarkRequest;
import site.ownw.homepage.controller.bookmark.model.CreateGroupRequest;
import site.ownw.homepage.controller.bookmark.model.SortBookmarkGroupRequest;
import site.ownw.homepage.controller.bookmark.model.SortBookmarkRequest;
import site.ownw.homepage.domain.bookmark.model.BookmarkGroupItem;
import site.ownw.homepage.domain.bookmark.repository.BookmarkGroupRepository;
import site.ownw.homepage.domain.bookmark.repository.BookmarkRepository;
import site.ownw.homepage.entity.Bookmark;
import site.ownw.homepage.entity.BookmarkGroup;

@Service
@Validated
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final BookmarkGroupRepository bookmarkGroupRepository;

    public void createGroup(Long userId, @Valid CreateGroupRequest request) {
        BookmarkGroup bookmarkGroup = new BookmarkGroup();
        bookmarkGroup.setName(request.getName());
        bookmarkGroup.setUserId(userId);
        bookmarkGroup.setSort(request.getSort());
        bookmarkGroupRepository.save(bookmarkGroup);
    }

    public void addBookmark(Long userId, Long bookmarkGroupId, @Valid AddBookmarkRequest request) {
        Bookmark bookmark = new Bookmark();
        bookmark.setBookmarkGroupId(bookmarkGroupId);
        bookmark.setUserId(userId);
        bookmark.setName(request.getName());
        bookmark.setUrl(request.getUrl());
        bookmark.setCleanUrl(cleanUrl(request.getUrl()));
        bookmark.setSort(request.getSort());
        bookmarkRepository.save(bookmark);
    }

    private String cleanUrl(String url) {
        URI uri = URI.create(url);
        return uri.getScheme() + "://" + uri.getHost();
    }

    public void deleteBookmark(Long bookmarkId) {
        bookmarkRepository.deleteById(bookmarkId);
    }

    @Transactional
    public void deleteGroup(Long bookmarkGroupId) {
        bookmarkGroupRepository.deleteById(bookmarkGroupId);
        bookmarkRepository.deleteAllByBookmarkGroupId(bookmarkGroupId);
    }

    public List<BookmarkGroupItem> getBookmarks(Long userId) {
        Sort sort = Sort.by(Sort.Order.asc("sort"), Sort.Order.desc("updatedAt"));
        return bookmarkGroupRepository.findAllByUserId(userId, sort).stream()
                .map(
                        group -> {
                            BookmarkGroupItem groupResponse = new BookmarkGroupItem();
                            groupResponse.setSort(group.getSort());
                            groupResponse.setName(group.getName());
                            groupResponse.setId(group.getId());
                            groupResponse.setBookmarks(
                                    bookmarkRepository.findAllByBookmarkGroupId(group.getId(), sort));
                            return groupResponse;
                        })
                .collect(Collectors.toList());
    }

    @Transactional
    public void sortBookmarkGroup(Long userId, @Valid SortBookmarkGroupRequest request) {
        Iterable<BookmarkGroup> groups =
                bookmarkGroupRepository.findAllById(request.getBookmarkGroupIds());
        int sort = 0;
        for (BookmarkGroup group : groups) {
            if (!group.getUserId().equals(userId)) {
                throw new AccessDeniedException("Not Allow");
            }
            group.setSort(sort++);
            bookmarkGroupRepository.save(group);
        }
    }

    @Transactional
    public void sortBookmark(Long bookmarkGroupId, @Valid SortBookmarkRequest request) {
        Iterable<Bookmark> bookmarks = bookmarkRepository.findAllById(request.getBookmarkIds());
        int sort = 0;
        for (Bookmark bookmark : bookmarks) {
            if (!bookmark.getBookmarkGroupId().equals(bookmarkGroupId)) {
                throw new AccessDeniedException("Not Allow");
            }
            bookmark.setSort(sort++);
            bookmarkRepository.save(bookmark);
        }
    }
}
