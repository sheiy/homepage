package site.ownw.homepage.domain.file.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.PagingAndSortingRepository;
import site.ownw.homepage.entity.UserFolder;

public interface UserFolderRepository extends PagingAndSortingRepository<UserFolder, Long> {

    Optional<UserFolder> findByUserIdAndParentFolderIdIsNull(Long userId);

    List<UserFolder> findAllByUserIdAndParentFolderId(Long userId, Long parentFolderId);

    Optional<UserFolder> findByPath(String path);
}
