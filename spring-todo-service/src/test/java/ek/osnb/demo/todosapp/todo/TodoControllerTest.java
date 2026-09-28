package ek.osnb.demo.todosapp.todo;

import ek.osnb.demo.todosapp.exceptions.GlobalExceptionHandler;
import ek.osnb.demo.todosapp.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoController.class)
@Import(GlobalExceptionHandler.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TodoService todoService;

    @Test
    void createShouldReturnCreatedTodo() throws Exception {
        when(todoService.create(new CreateTodoRequest("Write tests", 1L))).thenReturn(
                new TodoView(10L, 1L, "Write tests", false)
        );

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Write tests",
                                  "userId": 1
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void createShouldReturnBadRequestWhenServiceRejectsInput() throws Exception {
        when(todoService.create(new CreateTodoRequest(null, 1L)))
                .thenThrow(new IllegalArgumentException("Title cannot be null or blank"));

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": 1
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Request"))
                .andExpect(jsonPath("$.detail").value("Title cannot be null or blank"));
    }

    @Test
    void completeShouldReturnUpdatedTodo() throws Exception {
        when(todoService.complete(10L)).thenReturn(new TodoView(10L, 1L, "Write tests", true));

        mockMvc.perform(patch("/api/todos/10/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void deleteShouldReturnNoContent() throws Exception {
        doNothing().when(todoService).delete(10L);

        mockMvc.perform(delete("/api/todos/10"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    void findByIdShouldReturnNotFoundWhenServiceThrows() throws Exception {
        when(todoService.findById(99L)).thenThrow(new NotFoundException("Todo with id 99 not found"));

        mockMvc.perform(get("/api/todos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value("Todo with id 99 not found"));
    }

    @Test
    void findAllShouldReturnTodos() throws Exception {
        when(todoService.findAll()).thenReturn(List.of(new TodoView(10L, 1L, "Write tests", false)));

        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Write tests"));
    }
}
