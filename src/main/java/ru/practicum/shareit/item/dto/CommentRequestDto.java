package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotEmpty;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class CommentRequestDto {

    @NotEmpty
    private String text;

}