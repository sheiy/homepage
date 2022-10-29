package site.ownw.homepage.controller.bookmark;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import site.ownw.homepage.common.exception.EntityNotFoundException;
import site.ownw.homepage.controller.bookmark.model.AddBookmarkRequest;
import site.ownw.homepage.controller.bookmark.model.CreateGroupRequest;
import site.ownw.homepage.controller.bookmark.model.SortBookmarkGroupRequest;
import site.ownw.homepage.controller.bookmark.model.SortBookmarkRequest;
import site.ownw.homepage.controller.bookmark.model.UpdateBookmarkGroupRequest;
import site.ownw.homepage.controller.bookmark.model.UpdateBookmarkRequest;
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
    public List<BookmarkGroupItem> getBookmarks(
            @PathVariable @Schema(implementation = String.class) Long userId) {
        return bookmarkService.getBookmarks(userId);
    }

    @PreAuthorize("@authUtil.isMe(#userId)")
    @PostMapping("/api/v1/users/{userId}/bookmark-groups")
    public void createGroup(
            @PathVariable @Schema(implementation = String.class) Long userId,
            @Valid @RequestBody CreateGroupRequest request) {
        bookmarkService.createGroup(userId, request);
    }

    @PreAuthorize("@authUtil.isMe(#userId)")
    @PostMapping("/api/v1/users/{userId}/bookmark-groups/{bookmarkGroupId}/bookmarks")
    public void addBookmark(
            @PathVariable @Schema(implementation = String.class) Long userId,
            @PathVariable @Schema(implementation = String.class) Long bookmarkGroupId,
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
            @PathVariable @Schema(implementation = String.class) Long userId,
            @PathVariable @Schema(implementation = String.class) Long bookmarkGroupId,
            @PathVariable @Schema(implementation = String.class) Long bookmarkId) {
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
    public void deleteGroup(
            @PathVariable @Schema(implementation = String.class) Long userId,
            @PathVariable @Schema(implementation = String.class) Long bookmarkGroupId) {
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
    @PutMapping("/api/v1/users/{userId}/bookmark-groups/{bookmarkGroupId}")
    public void updateGroup(
            @PathVariable @Schema(implementation = String.class) Long userId,
            @PathVariable @Schema(implementation = String.class) Long bookmarkGroupId,
            @RequestBody @Valid UpdateBookmarkGroupRequest request) {
        BookmarkGroup bookmarkGroup =
                bookmarkGroupRepository
                        .findById(bookmarkGroupId)
                        .orElseThrow(() -> new EntityNotFoundException("BookmarkGroup", bookmarkGroupId));
        if (!bookmarkGroup.getUserId().equals(userId)) {
            throw new AccessDeniedException("Not Allow");
        }
        bookmarkService.UpdateBookmarkGroup(bookmarkGroupId, request);
    }

    @PreAuthorize("@authUtil.isMe(#userId)")
    @PutMapping("/api/v1/users/{userId}/bookmark-groups/{bookmarkGroupId}/bookmarks/{bookmarkId}")
    public void updateBookmark(
            @PathVariable @Schema(implementation = String.class) Long userId,
            @PathVariable @Schema(implementation = String.class) Long bookmarkGroupId,
            @PathVariable @Schema(implementation = String.class) Long bookmarkId,
            @RequestBody @Valid UpdateBookmarkRequest request) {
        Bookmark bookmark =
                bookmarkRepository
                        .findById(bookmarkId)
                        .orElseThrow(() -> new EntityNotFoundException("Bookmark", bookmarkId));
        if (!bookmark.getUserId().equals(userId)) {
            throw new AccessDeniedException("Not Allow");
        }
        bookmarkService.updateBookmark(bookmarkId, request);
    }

    @PreAuthorize("@authUtil.isMe(#userId)")
    @PostMapping("/api/v1/users/{userId}/bookmark-groups:sort")
    public void sortBookmarkGroup(
            @PathVariable @Schema(implementation = String.class) Long userId,
            @Valid @RequestBody SortBookmarkGroupRequest request) {
        bookmarkService.sortBookmarkGroup(userId, request);
    }

    @PreAuthorize("@authUtil.isMe(#userId)")
    @PostMapping("/api/v1/users/{userId}/bookmark-groups/{bookmarkGroupId}:sort")
    public void sortBookmarkGroup(
            @PathVariable @Schema(implementation = String.class) Long userId,
            @PathVariable @Schema(implementation = String.class) Long bookmarkGroupId,
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

    @PreAuthorize("@authUtil.isMe(#userId)")
    @PatchMapping(
            "/api/v1/users/{userId}/bookmark-groups/{bookmarkGroupId}/bookmarks/{bookmarkId}:updateFavicon")
    public void updateFavicon(
            @PathVariable @Schema(implementation = String.class) Long userId,
            @PathVariable @Schema(implementation = String.class) Long bookmarkGroupId,
            @PathVariable @Schema(implementation = String.class) Long bookmarkId,
            @Valid @RequestBody SortBookmarkRequest request) {
        Bookmark bookmark =
                bookmarkRepository
                        .findById(bookmarkId)
                        .orElseThrow(() -> new EntityNotFoundException("Bookmark", bookmarkId));
        if (!bookmark.getUserId().equals(userId)) {
            throw new AccessDeniedException("Not Allow");
        }
        bookmarkService.updateFavicon(bookmarkId);
    }
}
