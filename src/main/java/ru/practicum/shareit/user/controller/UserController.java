package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping
  public ResponseEntity<UserDto> create(@Valid @RequestBody UserDto userDto) {
    return new ResponseEntity<>(userService.create(userDto), HttpStatus.OK);
  }

  @PatchMapping("/{userId}")
  public ResponseEntity<UserDto> update(
          @PathVariable("userId") Long userId,
          @RequestBody UserDto userDto
  ) {
    return new ResponseEntity<>(userService.update(userId, userDto), HttpStatus.OK);
  }

  @GetMapping("/{userId}")
  public ResponseEntity<UserDto> getById(@PathVariable("userId") Long userId) {
    return new ResponseEntity<>(userService.getById(userId), HttpStatus.OK);
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> delete(@PathVariable("userId") Long userId) {
    userService.delete(userId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping
  public ResponseEntity<List<UserDto>> getAll() {
    return new ResponseEntity<>(userService.getAll(), HttpStatus.OK);
  }
}
