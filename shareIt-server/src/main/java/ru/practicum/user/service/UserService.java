package ru.practicum.user.service;

import ru.practicum.user.dto.UserDto;

import java.util.List;

public interface UserService {

  UserDto create(UserDto userDto);

  UserDto update(Long userId, UserDto userDto);

  UserDto getById(Long userId);

  void delete(Long userId);

  List<UserDto> getAll();
}
