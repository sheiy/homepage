package site.ownw.homepage.controller.bookmark;

import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import site.ownw.homepage.common.exception.EntityNotFoundException;
import site.ownw.homepage.controller.bookmark.model.AddBookmarkRequest;
import site.ownw.homepage.controller.bookmark.model.CreateGroupRequest;
import site.ownw.homepage.controller.bookmark.model.SortBookmarkGroupRequest;
import site.ownw.homepage.controller.bookmark.model.SortBookmarkRequest;
import site.ownw.homepage.domain.bookmark.BookmarkService;
import site.ownw.homepage.domain.bookmark.model.BookmarkGroupItem;
import site.ownw.homepage.domain.bookmark.repository.BookmarkGroupRepository;
import site.ownw.homepage.domain.bookmark.repository.BookmarkRepository;
import site.ownw.homepage.entity.Bookmark;
import site.ownw.homepage.entity.BookmarkGroup;

@RestController
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;
    private final BookmarkRepository bookmarkRepository;
    private final BookmarkGroupRepository bookmarkGroupRepository;

    @PreAuthorize("@authUtil.isMe(#userId)")
    @GetMapping("/api/v1/users/{userId}/bookmark-groups")
    public List<BookmarkGroupItem> getBookmarks(@PathVariable Long userId) {
        return bookmarkService.getBookmarks(userId);
    }

    @PreAuthorize("@authUtil.isMe(#userId)")
    @PostMapping("/api/v1/users/{userId}/bookmark-groups")
    public void createGroup(
            @PathVariable Long userId, @Valid @RequestBody CreateGroupRequest request) {
        bookmarkService.createGroup(userId, request);
    }

    @PreAuthorize("@authUtil.isMe(#userId)")
    @PostMapping("/api/v1/users/{userId}/bookmark-groups/{bookmarkGroupId}")
    public void addBookmark(
            @PathVariable Long userId,
            @PathVariable Long bookmarkGroupId,
            @Valid @RequestBody AddBookmarkRequest request) {
        BookmarkGroup bookmarkGroup =
                bookmarkGroupRepository
                        .findById(bookmarkGroupId)
                        .orElseThrow(() -> new EntityNotFoundException("BookmarkGroup", bookmarkGroupId));
        if (!bookmarkGroup.getUserId().equals(userId)) {
            throw new AccessDeniedException("Not Allow");
        }
        bookmarkService.addBookmark(userId, bookmarkGroupId, request);
    }

    @PreAuthorize("@authUtil.isMe(#userId)")
    @DeleteMapping("/api/v1/users/{userId}/bookmark-groups/{bookmarkGroupId}/bookmarks/{bookmarkId}")
    public void deleteBookmark(
            @PathVariable Long userId,
            @PathVariable Long bookmarkGroupId,
            @PathVariable Long bookmarkId) {
        Bookmark bookmark =
                bookmarkRepository
                        .findById(bookmarkId)
                        .orElseThrow(() -> new EntityNotFoundException("Bookmark", bookmarkId));
        if (!bookmark.getUserId().equals(userId)) {
            throw new AccessDeniedException("Not Allow");
        }
        bookmarkService.deleteBookmark(bookmarkId);
    }

    @PreAuthorize("@authUtil.isMe(#userId)")
    @DeleteMapping("/api/v1/users/{userId}/bookmark-groups/{bookmarkGroupId}")
    public void deleteGroup(@PathVariable Long userId, @PathVariable Long bookmarkGroupId) {
        BookmarkGroup bookmarkGroup =
                bookmarkGroupRepository
                        .findById(bookmarkGroupId)
                        .orElseThrow(() -> new EntityNotFoundException("BookmarkGroup", bookmarkGroupId));
        if (!bookmarkGroup.getUserId().equals(userId)) {
            throw new AccessDeniedException("Not Allow");
        }
        bookmarkService.deleteGroup(bookmarkGroupId);
    }

    @PreAuthorize("@authUtil.isMe(#userId)")
    @PostMapping("/api/v1/users/{userId}/bookmark-groups:sort")
    public void sortBookmarkGroup(
            @PathVariable Long userId, @Valid @RequestBody SortBookmarkGroupRequest request) {
        bookmarkService.sortBookmarkGroup(userId, request);
    }

    @PreAuthorize("@authUtil.isMe(#userId)")
    @PostMapping("/api/v1/users/{userId}/bookmark-groups/{bookmarkGroupId}:sort")
    public void sortBookmarkGroup(
            @PathVariable Long userId,
            @PathVariable Long bookmarkGroupId,
            @Valid @RequestBody SortBookmarkRequest request) {
        BookmarkGroup bookmarkGroup =
                bookmarkGroupRepository
                        .findById(bookmarkGroupId)
                        .orElseThrow(() -> new EntityNotFoundException("BookmarkGroup", bookmarkGroupId));
        if (!bookmarkGroup.getUserId().equals(userId)) {
            throw new AccessDeniedException("Not Allow");
        }
        bookmarkService.sortBookmark(bookmarkGroupId, request);
    }
}
