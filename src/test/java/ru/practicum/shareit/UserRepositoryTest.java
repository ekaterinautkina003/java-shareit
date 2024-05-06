package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Test
  void successCreate() {
    User user = createUser();
    userRepository.save(user);

    boolean actualExists = userRepository.existsByEmailIgnoreCase(user.getEmail());
    Optional<User> actualUser = userRepository.findById(1L);

    Assertions.assertTrue(actualExists);
    Assertions.assertTrue(actualUser.isPresent());
    Assertions.assertEquals(actualUser.get().getEmail(), user.getEmail());
    Assertions.assertEquals(actualUser.get().getName(), user.getName());
  }

  private User createUser() {
    User user = new User();
    user.setName("user_name1");
    user.setEmail("user_email1");
    return user;
  }
}
