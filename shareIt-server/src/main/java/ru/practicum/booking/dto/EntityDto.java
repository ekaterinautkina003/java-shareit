package ru.practicum.booking.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EntityDto {

  private Long id;
  private String name;
}
