package ru.practicum.item.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ItemFullDto {

  private Long id;
  private String name;
  private String description;
  private Boolean available;
  private ItemBookingDto lastBooking;
  private ItemBookingDto nextBooking;
  private List<CommentDto> comments;
}