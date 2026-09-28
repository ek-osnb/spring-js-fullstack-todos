package ek.osnb.demo.todosapp.user;

import ek.osnb.demo.todosapp.exceptions.NotFoundException;
import ek.osnb.demo.todosapp.exceptions.ResourceAlreadyExistsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserView create(CreateUserRequest request) {
        if (request.address() == null) {
            throw new IllegalArgumentException("Address cannot be null");
        }
        Address address = Address.of(
                request.address().street(),
                request.address().city(),
                request.address().zipCode(),
                request.address().country()
        );

        if (userRepository.existsByUsername(request.username())) {
            throw new ResourceAlreadyExistsException("User", "username", request.username());
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new ResourceAlreadyExistsException("User", "email", request.email());
        }

        User user = User.create(
                request.name(),
                request.username(),
                request.email(),
                address
        );

        return UserView.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserView findById(Long id) {
        return UserView.from(findUser(id));
    }

    @Transactional(readOnly = true)
    public List<UserView> findAll() {
        List<User> users = userRepository.findAll();
        List<UserView> userViews = new ArrayList<>();
        for (User user : users) {
            userViews.add(UserView.from(user));
        }
        return userViews;
    }

    public User findEntityById(Long id) {
        return findUser(id);
    }

    private User findUser(Long id) {
        Optional<User> byId = userRepository.findById(id);
        if (byId.isEmpty()) {
            throw new NotFoundException("User with id " + id + " not found");
        }
        return byId.get();
    }
}
