package ru.practicum.booking.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingRequestDto {
  private final Long itemId;
  private final LocalDateTime start;
  private final LocalDateTime end;
  private String status;
}