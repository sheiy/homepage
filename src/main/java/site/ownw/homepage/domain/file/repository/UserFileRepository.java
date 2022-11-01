package site.ownw.homepage.domain.file.repository;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import site.ownw.homepage.entity.UserFile;

public interface UserFileRepository extends PagingAndSortingRepository<UserFile, Long> {

    List<UserFile> findAllByUserIdAndFolderId(Long userId, Long folderId, Sort sort);

    @Modifying
    @Query("delete from `user_file` where folder_id = :folderId")
    void deleteAllByFolderId(Long folderId);
}
