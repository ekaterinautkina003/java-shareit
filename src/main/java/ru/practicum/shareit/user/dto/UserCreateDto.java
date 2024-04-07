package ru.practicum.shareit.user.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Setter
@Getter
public class UserCreateDto {
  private Long id;
  @NotEmpty
  @NotNull
  private String name;
  @NotEmpty
  @NotNull
  @Email
  private String email;
}
