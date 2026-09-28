package ek.osnb.demo.todosapp.user;

import ek.osnb.demo.todosapp.exceptions.NotFoundException;
import ek.osnb.demo.todosapp.exceptions.ResourceAlreadyExistsException;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void createShouldPersistUniqueUser() {
        CreateUserRequest request = new CreateUserRequest(
                "Ada Lovelace",
                "ada",
                "ada@example.com",
                new AddressRequest("Main St", "Copenhagen", "2100", "Denmark")
        );
        when(userRepository.existsByUsername("ada")).thenReturn(false);
        when(userRepository.existsByEmail("ada@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            ReflectionTestUtils.setField(user, "id", 1L);
            return user;
        });

        UserView result = userService.create(request);

        assertAll(
                () -> assertEquals(1L, result.id()),
                () -> assertEquals("Ada Lovelace", result.name()),
                () -> assertEquals("ada", result.username()),
                () -> assertEquals("ada@example.com", result.email()),
                () -> assertEquals("Main St", result.address().street())
        );
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createShouldRejectNullAddress() {
        CreateUserRequest request = new CreateUserRequest("Ada Lovelace", "ada", "ada@example.com", null);

        assertThrows(IllegalArgumentException.class, () -> userService.create(request));
    }

    @Test
    void createShouldRejectDuplicateUsername() {
        CreateUserRequest request = new CreateUserRequest(
                "Ada Lovelace",
                "ada",
                "ada@example.com",
                new AddressRequest("Main St", "Copenhagen", "2100", "Denmark")
        );
        when(userRepository.existsByUsername("ada")).thenReturn(true);

        ResourceAlreadyExistsException ex = assertThrows(
                ResourceAlreadyExistsException.class,
                () -> userService.create(request)
        );

        assertEquals("User with username 'ada' already exists", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createShouldRejectDuplicateEmail() {
        CreateUserRequest request = new CreateUserRequest(
                "Ada Lovelace",
                "ada",
                "ada@example.com",
                new AddressRequest("Main St", "Copenhagen", "2100", "Denmark")
        );
        when(userRepository.existsByUsername("ada")).thenReturn(false);
        when(userRepository.existsByEmail("ada@example.com")).thenReturn(true);

        ResourceAlreadyExistsException ex = assertThrows(
                ResourceAlreadyExistsException.class,
                () -> userService.create(request)
        );

        assertEquals("User with email 'ada@example.com' already exists", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void findByIdShouldReturnView() {
        User user = user("Ada Lovelace", "ada", "ada@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserView result = userService.findById(1L);

        assertEquals(1L, result.id());
        assertEquals("Ada Lovelace", result.name());
    }

    @Test
    void findByIdShouldRejectMissingUser() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> userService.findById(99L));

        assertEquals("User with id 99 not found", ex.getMessage());
    }

    @Test
    void findAllShouldMapUsers() {
        User first = user("Ada Lovelace", "ada", "ada@example.com");
        User second = user("Grace Hopper", "grace", "grace@example.com");
        when(userRepository.findAll()).thenReturn(List.of(first, second));

        List<UserView> result = userService.findAll();

        assertEquals(2, result.size());
        assertEquals("Ada Lovelace", result.getFirst().name());
        assertEquals("Grace Hopper", result.get(1).name());
    }

    @Test
    void findEntityByIdShouldReturnUser() {
        User user = user("Ada Lovelace", "ada", "ada@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findEntityById(1L);

        assertSame(user, result);
    }

    private static User user(String name, String username, String email) {
        User user = User.create(name, username, email, Address.of("Main St", "Copenhagen", "2100", "Denmark"));
        ReflectionTestUtils.setField(user, "id", 1L);
        return user;
    }
}
