package site.ownw.homepage.domain.user.repository;

import java.util.Optional;
import org.springframework.data.repository.PagingAndSortingRepository;
import site.ownw.homepage.entity.Token;

public interface TokenRepository extends PagingAndSortingRepository<Token, Long> {

    Optional<Token> findFirstByBearer(String bearer);
}
