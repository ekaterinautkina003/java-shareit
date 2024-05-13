package ru.practicum.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
public class ItemFullDto {

  private Long id;
  @NotEmpty
  private String name;
  @NotEmpty
  private String description;
  @NotNull
  private Boolean available;
  private ItemBookingDto lastBooking;
  private ItemBookingDto nextBooking;
  private List<CommentDto> comments;
}