package site.ownw.homepage.domain.system.repository;

import java.util.Optional;
import org.springframework.data.repository.PagingAndSortingRepository;
import site.ownw.homepage.entity.SystemConfig;

public interface SystemConfigRepository extends PagingAndSortingRepository<SystemConfig, Long> {

    Optional<SystemConfig> findByKey(String key);
}
