package site.ownw.homepage.domain.user.repository;

import java.util.Optional;
import org.springframework.data.repository.PagingAndSortingRepository;
import site.ownw.homepage.entity.UserConfig;

public interface UserConfigRepository extends PagingAndSortingRepository<UserConfig, Long> {

    Optional<UserConfig> findByUserIdAndKey(Long userId, String key);
}
