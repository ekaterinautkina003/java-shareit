package ru.practicum.booking.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingRequestDto {
  @NotNull
  private final Long itemId;
  @NotNull
  @FutureOrPresent
  private final LocalDateTime start;
  @NotNull
  @FutureOrPresent
  private final LocalDateTime end;
  private String status;
}