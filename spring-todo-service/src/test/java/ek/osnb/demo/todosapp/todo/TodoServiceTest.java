package ek.osnb.demo.todosapp.todo;

import ek.osnb.demo.todosapp.exceptions.NotFoundException;
import ek.osnb.demo.todosapp.user.Address;
import ek.osnb.demo.todosapp.user.User;
import ek.osnb.demo.todosapp.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TodoService todoService;

    @Test
    void createShouldPersistTodoForUser() {
        User user = user(1L);
        when(userService.findEntityById(1L)).thenReturn(user);
        when(todoRepository.save(any(Todo.class))).thenAnswer(invocation -> {
            Todo todo = invocation.getArgument(0);
            ReflectionTestUtils.setField(todo, "id", 11L);
            return todo;
        });

        TodoView result = todoService.create(new CreateTodoRequest("Write tests", 1L));

        assertAll(
                () -> assertEquals(11L, result.id()),
                () -> assertEquals(1L, result.userId()),
                () -> assertEquals("Write tests", result.title()),
                () -> assertFalse(result.completed())
        );
        verify(todoRepository).save(any(Todo.class));
    }

    @Test
    void createShouldRejectNullUserId() {
        assertThrows(IllegalArgumentException.class,
                () -> todoService.create(new CreateTodoRequest("Write tests", null)));
    }

    @Test
    void findByIdShouldReturnView() {
        Todo todo = todo(11L, user(1L), "Write tests");
        when(todoRepository.findById(11L)).thenReturn(Optional.of(todo));

        TodoView result = todoService.findById(11L);

        assertEquals("Write tests", result.title());
        assertEquals(1L, result.userId());
    }

    @Test
    void findByIdShouldRejectMissingTodo() {
        when(todoRepository.findById(99L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> todoService.findById(99L));

        assertEquals("Todo with id 99 not found", ex.getMessage());
    }

    @Test
    void findAllShouldMapTodos() {
        Todo first = todo(1L, user(1L), "First");
        Todo second = todo(2L, user(1L), "Second");
        when(todoRepository.findAll()).thenReturn(List.of(first, second));

        List<TodoView> result = todoService.findAll();

        assertEquals(2, result.size());
        assertEquals("First", result.get(0).title());
        assertEquals("Second", result.get(1).title());
    }

    @Test
    void findByUserIdShouldMapUsersTodos() {
        User user = user(1L);
        when(userService.findEntityById(1L)).thenReturn(user);
        when(todoRepository.findByUserId(1L)).thenReturn(List.of(
                todo(1L, user, "First"),
                todo(2L, user, "Second")
        ));

        List<TodoView> result = todoService.findByUserId(1L);

        assertEquals(2, result.size());
        assertEquals("First", result.get(0).title());
        assertEquals(1L, result.get(1).userId());
    }

    @Test
    void findByUserIdShouldRejectMissingUser() {
        when(userService.findEntityById(99L)).thenThrow(new NotFoundException("User with id 99 not found"));

        assertThrows(NotFoundException.class, () -> todoService.findByUserId(99L));
    }

    @Test
    void completeShouldMarkTodoComplete() {
        Todo todo = todo(11L, user(1L), "Write tests");
        when(todoRepository.findById(11L)).thenReturn(Optional.of(todo));

        TodoView result = todoService.complete(11L);

        assertTrue(result.completed());
    }

    @Test
    void reopenShouldMarkTodoIncomplete() {
        Todo todo = todo(11L, user(1L), "Write tests");
        todo.complete();
        when(todoRepository.findById(11L)).thenReturn(Optional.of(todo));

        TodoView result = todoService.reopen(11L);

        assertFalse(result.completed());
    }

    @Test
    void updateShouldChangeTitleAndCompletion() {
        Todo todo = todo(11L, user(1L), "Write tests");
        todo.complete();
        when(todoRepository.findById(11L)).thenReturn(Optional.of(todo));

        TodoView result = todoService.update(11L, new UpdateTodoRequest("Write more tests", false));

        assertEquals("Write more tests", result.title());
        assertFalse(result.completed());
    }

    @Test
    void deleteShouldRemoveTodo() {
        Todo todo = todo(11L, user(1L), "Write tests");
        when(todoRepository.findById(11L)).thenReturn(Optional.of(todo));

        todoService.delete(11L);

        verify(todoRepository).delete(todo);
    }

    @Test
    void updateShouldRejectMissingTodo() {
        when(todoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> todoService.update(99L, new UpdateTodoRequest("x", null)));
    }

    private static User user(Long id) {
        User user = User.create("Ada Lovelace", "ada", "ada@example.com", Address.of("Main St", "Copenhagen", "2100", "Denmark"));
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private static Todo todo(Long id, User user, String title) {
        Todo todo = Todo.create(title, user);
        ReflectionTestUtils.setField(todo, "id", id);
        return todo;
    }
}
