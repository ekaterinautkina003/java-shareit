package ru.practicum.request.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class ItemRequestShortDto {
  @NotEmpty
  private String description;
}