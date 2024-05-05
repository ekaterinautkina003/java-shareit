package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        User result = null;
        try {
            result = userRepository.save(userMapper.toUser(userDto));
        } catch (Exception e) {
            throw new AlreadyExistsException();
        }
        return userMapper.toUserDto(result);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User result = userRepository.findById(userId)
                .orElseThrow(NotFoundException::new);
        if (userDto.getEmail() != null && userDto.getEmail().equals(result.getEmail())) {
            return getById(userId);
        }
        userDto.setId(userId);
        if (userDto.getName() != null) {
            result.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            result.setEmail(userDto.getEmail());
        }
        return userMapper.toUserDto(userRepository.save(result));
    }

    @Override
    public UserDto getById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(NotFoundException::new);
        return userMapper.toUserDto(user);
    }

    @Override
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserDto> getAll() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
