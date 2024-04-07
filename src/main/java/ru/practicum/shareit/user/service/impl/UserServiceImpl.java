package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    public UserDto create(UserCreateDto userDto) {
        User user = userMapper.toUser(userDto);
        return userMapper.toUserDto(userRepository.create(user));
    }

    @Override
    public UserDto update(Long userId, UserUpdateDto userDto) {
        User user = userMapper.toUser(userDto);
        return userMapper.toUserDto(userRepository.update(userId, user));
    }

    @Override
    public UserDto getById(Long userId) {
        return userMapper.toUserDto(userRepository.getById(userId));
    }

    @Override
    public void delete(Long userId) {
        userRepository.delete(userId);
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.getAll()
                .stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
