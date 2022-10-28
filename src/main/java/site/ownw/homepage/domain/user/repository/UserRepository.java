package site.ownw.homepage.domain.user.repository;

import java.util.Optional;
import org.springframework.data.repository.PagingAndSortingRepository;
import site.ownw.homepage.entity.User;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    Optional<User> findByNickname(String nickName);
}
