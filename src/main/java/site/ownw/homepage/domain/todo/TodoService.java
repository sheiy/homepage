package site.ownw.homepage.domain.todo;

import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import site.ownw.homepage.common.enums.TodoStatus;
import site.ownw.homepage.common.exception.EntityNotFoundException;
import site.ownw.homepage.controller.todo.model.CreateTodoRequest;
import site.ownw.homepage.controller.todo.model.UpdateTodoRequest;
import site.ownw.homepage.domain.todo.repository.TodoRepository;
import site.ownw.homepage.entity.Todo;

@Service
@Validated
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    public List<Todo> getTodoList(Long userId, TodoStatus status) {
        Sort sort =
                Sort.by(
                        Sort.Order.asc("status"),
                        Sort.Order.desc("createdAt"));
        if (status == null) {
            return todoRepository.findAllByUserId(userId, sort);
        } else {
            return todoRepository.findAllByUserIdAndStatus(userId, status, sort);
        }
    }

    public void create(Long userId, @Valid CreateTodoRequest request) {
        Todo todo = new Todo();
        todo.setUserId(userId);
        todo.setContent(request.getContent());
        todo.setStatus(TodoStatus.CREATED);
        todo.setRemindDateTime(request.getRemindDateTime());
        this.todoRepository.save(todo);
    }

    public void update(Long todoId, @Valid UpdateTodoRequest request) {
        Todo todo =
                todoRepository
                        .findById(todoId)
                        .orElseThrow(() -> new EntityNotFoundException("Todo", todoId));
        todo.setContent(request.getContent());
        todo.setStatus(request.getStatus());
        todo.setRemindDateTime(request.getRemindDateTime());
        this.todoRepository.save(todo);
    }

    public void delete(Long todoId) {
        todoRepository.deleteById(todoId);
    }
}
