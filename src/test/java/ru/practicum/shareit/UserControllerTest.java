package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.GlobalExceptionHandler;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@ContextConfiguration(classes = {UserController.class, GlobalExceptionHandler.class})
public class UserControllerTest {

  private static final String URL = "http://localhost:8080/users";
  @Autowired
  ObjectMapper mapper;
  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private UserService userService;

  @Test
  @SneakyThrows
  void emptyName() {
    User user = User.builder()
            .email("fas@afas.com")
            .build();
    ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
            .header("Content-Type", "application/json")
            .header("X-Sharer-User-Id", 1L)
            .content(mapper.writeValueAsString(user)));
    response.andExpect(status().is4xxClientError());
  }

  @Test
  @SneakyThrows
  void nullEmail() {
    User user = User.builder()
            .name("test")
            .build();
    ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
            .header("Content-Type", "application/json")
            .header("X-Sharer-User-Id", 1L)
            .content(mapper.writeValueAsString(user)));
    response.andExpect(status().is4xxClientError());
  }

  @Test
  @SneakyThrows
  void saveIncorrectEmail() {
    User user = User.builder()
            .name("test")
            .email("test")
            .build();
    ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
            .header("Content-Type", "application/json")
            .header("X-Sharer-User-Id", 1L)
            .content(mapper.writeValueAsString(user)));
    response.andExpect(status().is4xxClientError());
  }

  @Test
  @SneakyThrows
  void duplicateUserEmail() {
    when(userService.create(Mockito.any()))
            .thenThrow(AlreadyExistsException.class);
    UserDto user = createUserDto();
    ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
            .header("Content-Type", "application/json")
            .header("X-Sharer-User-Id", 1L)
            .content(mapper.writeValueAsString(user)));
    response.andExpect(status().is4xxClientError());
  }

  @Test
  @SneakyThrows
  void updateUserNotFound() {
    when(userService.update(Mockito.anyLong(), Mockito.any()))
            .thenThrow(NotFoundException.class);
    UserDto user = createUserDto();
    ResultActions response = mockMvc.perform(MockMvcRequestBuilders.patch(URL.concat("/{id}"), 1L)
            .header("Content-Type", "application/json")
            .header("X-Sharer-User-Id", 1L)
            .content(mapper.writeValueAsString(user)));
    response.andExpect(status().is4xxClientError());
  }

  @Test
  @SneakyThrows
  void successCreate() {
    UserDto user = createUserDto();
    when(userService.create(Mockito.any()))
            .thenReturn(user);
    ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
            .header("Content-Type", "application/json")
            .header("X-Sharer-User-Id", 1L)
            .content(mapper.writeValueAsString(user)));
    response.andExpect(status().isOk());
  }

  private UserDto createUserDto() {
    return UserDto.builder()
            .name("test")
            .email("test@test.ru")
            .build();
  }
}