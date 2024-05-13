package ru.practicum.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.item.dto.ItemDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemRequestDto {

  @NotNull
  private Long id;
  @NotEmpty
  private String description;
  @NotNull
  private LocalDateTime created;
  private List<ItemDto> items;
}