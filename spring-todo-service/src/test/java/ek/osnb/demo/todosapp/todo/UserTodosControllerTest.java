package ek.osnb.demo.todosapp.todo;

import ek.osnb.demo.todosapp.exceptions.GlobalExceptionHandler;
import ek.osnb.demo.todosapp.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserTodosController.class)
@Import(GlobalExceptionHandler.class)
class loUserTodosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TodoService todoService;

    @Test
    void findByUserIdShouldReturnUsersTodos() throws Exception {
        when(todoService.findByUserId(1L)).thenReturn(List.of(
                new TodoView(10L, 1L, "Write tests", false),
                new TodoView(11L, 1L, "Ship it", true)
        ));

        mockMvc.perform(get("/api/users/1/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Write tests"))
                .andExpect(jsonPath("$[1].completed").value(true));
    }

    @Test
    void findByUserIdShouldReturnNotFoundForUnknownUser() throws Exception {
        when(todoService.findByUserId(99L)).thenThrow(new NotFoundException("User with id 99 not found"));

        mockMvc.perform(get("/api/users/99/todos"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("User with id 99 not found"));
    }
}
