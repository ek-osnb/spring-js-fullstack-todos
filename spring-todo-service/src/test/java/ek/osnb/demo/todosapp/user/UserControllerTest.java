package ek.osnb.demo.todosapp.user;

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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void createShouldReturnCreatedUser() throws Exception {
        when(userService.create(new CreateUserRequest(
                "Ada Lovelace",
                "ada",
                "ada@example.com",
                new AddressRequest("Main St", "Copenhagen", "2100", "Denmark")
        ))).thenReturn(new UserView(
                1L,
                "Ada Lovelace",
                "ada",
                "ada@example.com",
                new AddressView("Main St", "Copenhagen", "2100", "Denmark")
        ));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Ada Lovelace",
                                  "username": "ada",
                                  "email": "ada@example.com",
                                  "address": {
                                    "street": "Main St",
                                    "city": "Copenhagen",
                                    "zipCode": "2100",
                                    "country": "Denmark"
                                  }
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("ada"))
                .andExpect(jsonPath("$.address.city").value("Copenhagen"));
    }

    @Test
    void createShouldReturnBadRequestWhenServiceRejectsInput() throws Exception {
        when(userService.create(new CreateUserRequest("Ada Lovelace", "ada", "ada@example.com", null)))
                .thenThrow(new IllegalArgumentException("Address cannot be null"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Ada Lovelace",
                                  "username": "ada",
                                  "email": "ada@example.com"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Request"))
                .andExpect(jsonPath("$.detail").value("Address cannot be null"));
    }

    @Test
    void findByIdShouldReturnNotFoundWhenServiceThrows() throws Exception {
        when(userService.findById(99L)).thenThrow(new NotFoundException("User with id 99 not found"));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value("User with id 99 not found"));
    }

    @Test
    void findAllShouldReturnUsers() throws Exception {
        when(userService.findAll()).thenReturn(List.of(
                new UserView(1L, "Ada Lovelace", "ada", "ada@example.com", new AddressView("Main St", "Copenhagen", "2100", "Denmark"))
        ));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Ada Lovelace"));
    }
}
