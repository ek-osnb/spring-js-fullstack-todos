package ek.osnb.demo.todosapp.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void createShouldPopulateFields() {
        Address address = Address.of("Main St", "Copenhagen", "2100", "Denmark");

        User user = User.create("Ada Lovelace", "ada", "ada@example.com", address);

        assertAll(
                () -> assertEquals("Ada Lovelace", user.getName()),
                () -> assertEquals("ada", user.getUsername()),
                () -> assertEquals("ada@example.com", user.getEmail()),
                () -> assertSame(address, user.getAddress())
        );
    }

    @Test
    void createShouldRejectBlankValues() {
        Address address = Address.of("Main St", "Copenhagen", "2100", "Denmark");

        assertThrows(IllegalArgumentException.class, () -> User.create(null, "ada", "ada@example.com", address));
        assertThrows(IllegalArgumentException.class, () -> User.create("   ", "ada", "ada@example.com", address));
        assertThrows(IllegalArgumentException.class, () -> User.create("Ada", null, "ada@example.com", address));
        assertThrows(IllegalArgumentException.class, () -> User.create("Ada", "   ", "ada@example.com", address));
        assertThrows(IllegalArgumentException.class, () -> User.create("Ada", "ada", null, address));
        assertThrows(IllegalArgumentException.class, () -> User.create("Ada", "ada", "   ", address));
        assertThrows(IllegalArgumentException.class, () -> User.create("Ada", "ada", "ada@example.com", null));
    }

    @Test
    void changeAddressShouldUpdateAddress() {
        Address address = Address.of("Main St", "Copenhagen", "2100", "Denmark");
        Address newAddress = Address.of("Other St", "Aarhus", "8000", "Denmark");
        User user = User.create("Ada Lovelace", "ada", "ada@example.com", address);

        user.changeAddress(newAddress);

        assertSame(newAddress, user.getAddress());
    }

    @Test
    void changeEmailShouldUpdateEmail() {
        Address address = Address.of("Main St", "Copenhagen", "2100", "Denmark");
        User user = User.create("Ada Lovelace", "ada", "ada@example.com", address);

        user.changeEmail("new@example.com");

        assertEquals("new@example.com", user.getEmail());
    }

    @Test
    void changeAddressShouldRejectNull() {
        Address address = Address.of("Main St", "Copenhagen", "2100", "Denmark");
        User user = User.create("Ada Lovelace", "ada", "ada@example.com", address);

        assertThrows(IllegalArgumentException.class, () -> user.changeAddress(null));
    }

    @Test
    void changeEmailShouldRejectNullOrBlank() {
        Address address = Address.of("Main St", "Copenhagen", "2100", "Denmark");
        User user = User.create("Ada Lovelace", "ada", "ada@example.com", address);

        assertThrows(IllegalArgumentException.class, () -> user.changeEmail(null));
        assertThrows(IllegalArgumentException.class, () -> user.changeEmail("   "));
    }
}