package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@Jacksonized
public class CommentRequestDto {

    @NotEmpty
    private String text;

}