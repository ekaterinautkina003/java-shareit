package ru.practicum.shareit.user.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;

/**
 * TODO Sprint add-controllers.
 */
@Setter
@Getter
public class UserUpdateDto {
    private Long id;
    private String name;
    @Email
    private String email;
}
