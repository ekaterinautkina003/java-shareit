package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    @MockBean
    private UserRepository userRepository;

    @Test
    void duplicateEmail() {
        UserDto user = createUserDto();

        when(userRepository.save(any()))
                .thenThrow(AlreadyExistsException.class);

        assertThrows(AlreadyExistsException.class, () -> userService.create(user));
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void successCreate() {
        UserDto user = createUserDto();
        when(userRepository.save(any()))
                .thenReturn(userMapper.toUser(user));

        var result = userService.create(user);

        assertThat(result).usingRecursiveComparison().isEqualTo(user);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void patchNotFoundUser() {
        UserDto user = createUserDto();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.update(1L, user));
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void emailWithoutChanges() {
        UserDto user = createUserDto();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(User.builder()
                        .id(1L)
                        .name("test")
                        .email("test@test.com")
                        .build()));

        var result = userService.update(1L, user);

        assertThat(result).usingRecursiveComparison().isEqualTo(user);
        verify(userRepository, times(2)).findById(anyLong());
    }

    @Test
    void patchAllFields() {
        UserDto user = createUserDto();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(User.builder()
                        .id(1L)
                        .name("first")
                        .email("first@test.com")
                        .build()));
        when(userRepository.save(any()))
                .thenReturn(userMapper.toUser(user));

        UserDto result = userService.update(1L, user);

        assertThat(result).usingRecursiveComparison().isEqualTo(user);
        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void getAll() {
        UserDto user = createUserDto();
        when(userRepository.findAll())
                .thenReturn(List.of(userMapper.toUser(user)));

        List<UserDto> result = userService.getAll();

        assertThat(result).usingRecursiveComparison().isEqualTo(List.of(user));
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getById() {
        UserDto user = createUserDto();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userMapper.toUser(user)));

        UserDto result = userService.getById(1L);

        assertThat(result).usingRecursiveComparison().isEqualTo(user);
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void notFoundException() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getById(1L));
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void delete() {
        userService.delete(1L);

        verify(userRepository, times(1)).deleteById(anyLong());
    }

    private UserDto createUserDto() {
        return UserDto.builder()
                .id(1L)
                .name("test")
                .email("test@test.com")
                .build();
    }
}