package site.ownw.homepage.controller.todo;

import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.ownw.homepage.common.enums.TodoStatus;
import site.ownw.homepage.common.exception.EntityNotFoundException;
import site.ownw.homepage.controller.todo.model.CreateTodoRequest;
import site.ownw.homepage.controller.todo.model.UpdateTodoRequest;
import site.ownw.homepage.domain.todo.TodoService;
import site.ownw.homepage.domain.todo.repository.TodoRepository;
import site.ownw.homepage.entity.Todo;

@RestController
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;
    private final TodoRepository todoRepository;

    @PreAuthorize("@authUtil.isMe(#userId)")
    @GetMapping("/api/v1/users/{userId}/todos")
    public List<Todo> todos(
            @PathVariable Long userId, @RequestParam(required = false) TodoStatus status) {
        return todoService.getTodoList(userId, status);
    }

    @PostMapping("/api/v1/users/{userId}/todos")
    public void create(@PathVariable Long userId, @Valid @RequestBody CreateTodoRequest request) {
        todoService.create(userId, request);
    }

    @PreAuthorize("@authUtil.isMe(#userId)")
    @PutMapping("/api/v1/users/{userId}/todos/{todoId}/")
    public void update(
            @PathVariable Long userId,
            @PathVariable Long todoId,
            @Valid @RequestBody UpdateTodoRequest request) {
        Todo todo =
                todoRepository
                        .findById(todoId)
                        .orElseThrow(() -> new EntityNotFoundException("Todo", todoId));
        if (!todo.getUserId().equals(userId)) {
            throw new AccessDeniedException("Not Allow");
        }
        todoService.update(todoId, request);
    }
}
