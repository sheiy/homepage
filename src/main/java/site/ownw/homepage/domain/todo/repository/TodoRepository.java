package site.ownw.homepage.domain.todo.repository;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import site.ownw.homepage.common.enums.TodoStatus;
import site.ownw.homepage.entity.Todo;

public interface TodoRepository extends PagingAndSortingRepository<Todo, Long> {

    List<Todo> findAllByUserId(Long ownerId, Sort sort);

    List<Todo> findAllByUserIdAndStatus(Long userId, TodoStatus status, Sort sort);
}
