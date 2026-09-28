package ek.osnb.demo.todosapp.todo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class UserTodosController {

    private final TodoService todoService;

    UserTodosController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping("/api/users/{userId}/todos")
    List<TodoView> findByUserId(@PathVariable Long userId) {
        return todoService.findByUserId(userId);
    }
}
