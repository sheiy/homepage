package site.ownw.homepage.domain.bookmark;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import site.ownw.homepage.common.exception.BusinessException;
import site.ownw.homepage.common.exception.EntityNotFoundException;
import site.ownw.homepage.controller.bookmark.model.AddBookmarkRequest;
import site.ownw.homepage.controller.bookmark.model.CreateGroupRequest;
import site.ownw.homepage.controller.bookmark.model.SortBookmarkGroupRequest;
import site.ownw.homepage.controller.bookmark.model.SortBookmarkRequest;
import site.ownw.homepage.controller.bookmark.model.UpdateBookmarkGroupRequest;
import site.ownw.homepage.controller.bookmark.model.UpdateBookmarkRequest;
import site.ownw.homepage.domain.bookmark.model.BookmarkGroupItem;
import site.ownw.homepage.domain.bookmark.repository.BookmarkGroupRepository;
import site.ownw.homepage.domain.bookmark.repository.BookmarkRepository;
import site.ownw.homepage.entity.Bookmark;
import site.ownw.homepage.entity.BookmarkGroup;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final BookmarkGroupRepository bookmarkGroupRepository;
    private final HttpClient httpClient;
    private final ThreadPoolExecutor threadPoolExecutor;

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
        updateFaviconAsync(bookmark.getId(), bookmark.getCleanUrl());
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

    public void UpdateBookmarkGroup(Long bookmarkGroupId, @Valid UpdateBookmarkGroupRequest request) {
        BookmarkGroup bookmarkGroup =
                bookmarkGroupRepository
                        .findById(bookmarkGroupId)
                        .orElseThrow(() -> new EntityNotFoundException("BookmarkGroup", bookmarkGroupId));
        bookmarkGroup.setName(request.getName());
        bookmarkGroupRepository.save(bookmarkGroup);
    }

    public void updateBookmark(Long bookmarkId, @Valid UpdateBookmarkRequest request) {
        Bookmark bookmark =
                bookmarkRepository
                        .findById(bookmarkId)
                        .orElseThrow(() -> new EntityNotFoundException("Bookmark", bookmarkId));
        bookmark.setName(request.getName());
        bookmark.setUrl(request.getUrl());
        bookmark.setCleanUrl(cleanUrl(request.getUrl()));
        bookmarkRepository.save(bookmark);
        updateFaviconAsync(bookmark.getId(), bookmark.getCleanUrl());
    }

    private void updateFaviconAsync(Long bookmarkId, String cleanUrl) {
        threadPoolExecutor.submit(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            bookmarkRepository.updateFaviconById(bookmarkId, getFavicon(cleanUrl));
                        } catch (Throwable t) {
                            log.error("update bookmark:{} error.", bookmarkId, t);
                        }
                    }
                });
    }

    private String getFavicon(String cleanUrl)
            throws IOException, URISyntaxException, InterruptedException {
        URI uri = new URI(cleanUrl);
        String href;
        Document doc = Jsoup.connect(cleanUrl).get();
        Element icoElement = doc.head().select("link[href~=.*\\.(ico|png|svg)]").first();
        if (icoElement != null) {
            href = icoElement.attr("href");
            if (href.startsWith("//")) {
                href = uri.getScheme() + ":" + href;
            }
        } else {
            href = cleanUrl + "/favicon.ico";
        }
        if (href.startsWith("/")) {
            href = uri.getScheme() + "://" + uri.getHost() + href;
        }
        HttpRequest request = HttpRequest.newBuilder().uri(new URI(href)).GET().build();
        int i = httpClient.send(request, HttpResponse.BodyHandlers.discarding()).statusCode();
        if (HttpStatus.valueOf(i).is2xxSuccessful()) {
            return href;
        }
        return null;
    }

    public void updateFavicon(Long bookmarkId) {
        try {
            Bookmark bookmark =
                    bookmarkRepository
                            .findById(bookmarkId)
                            .orElseThrow(() -> new EntityNotFoundException("Bookmark", bookmarkId));
            bookmark.setFavicon(getFavicon(bookmark.getCleanUrl()));
            bookmarkRepository.save(bookmark);
        } catch (IOException | URISyntaxException | InterruptedException e) {
            throw new BusinessException("can not find favicon.", e.getLocalizedMessage());
        }
    }
}
