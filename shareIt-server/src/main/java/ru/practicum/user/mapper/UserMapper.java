package ru.practicum.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;

@Component
public class UserMapper {

  public UserDto toUserDto(User user) {
    return UserDto.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .build();
  }

  public User toUser(UserDto dto) {
    return User.builder()
            .id(dto.getId())
            .name(dto.getName())
            .email(dto.getEmail())
            .build();
  }
}
