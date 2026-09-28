package ek.osnb.demo.todosapp.todo;

import ek.osnb.demo.todosapp.exceptions.NotFoundException;
import ek.osnb.demo.todosapp.user.User;
import ek.osnb.demo.todosapp.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserService userService;

    TodoService(
            TodoRepository todoRepository,
            UserService userService
    ) {
        this.todoRepository = todoRepository;
        this.userService = userService;
    }

    public TodoView create(CreateTodoRequest request) {
        if (request.userId() == null) {
            throw new IllegalArgumentException("User id cannot be null");
        }
        User user = userService.findEntityById(request.userId());

        Todo todo = Todo.create(
                request.title(),
                user
        );

        Todo saved = todoRepository.save(todo);

        return TodoView.from(saved);
    }

    @Transactional(readOnly = true)
    public TodoView findById(Long id) {
        return TodoView.from(findTodo(id));
    }

    @Transactional(readOnly = true)
    public List<TodoView> findAll() {
        List<Todo> todos = todoRepository.findAll();
        List<TodoView> todoViews = new ArrayList<>();
        for (Todo todo : todos) {
            todoViews.add(TodoView.from(todo));
        }
        return todoViews;
    }

    public TodoView complete(Long id) {
        Todo todo = findTodo(id);
        todo.complete();

        return TodoView.from(todo);
    }

    public TodoView reopen(Long id) {
        Todo todo = findTodo(id);
        todo.reopen();

        return TodoView.from(todo);
    }

    public void delete(Long id) {
        Todo todo = findTodo(id);
        todoRepository.delete(todo);
    }

    public TodoView update(Long id, UpdateTodoRequest request) {
        Todo todo = findTodo(id);

        todo.updateTitle(request.title());
        if (request.completed() != null) {
            if (request.completed()) {
                todo.complete();
            } else {
                todo.reopen();
            }
        }

        return TodoView.from(todo);
    }

    private Todo findTodo(Long id) {
        Optional<Todo> byId = todoRepository.findById(id);
        if (byId.isEmpty()) {
            throw new NotFoundException("Todo with id " + id + " not found");
        }
        return byId.get();
    }
}
