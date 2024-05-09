package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemMapperTest {

  private final ItemMapper itemMapper = new ItemMapper();

  @Test
  void toDTOWithoutRequestTest() {
    Item item = getDefaultItem();

    var result = itemMapper.toItemDto(item);
    assertThat(result.getId()).isEqualTo(item.getId());
    assertThat(result.getName()).isEqualTo(item.getName());
    assertThat(result.getDescription()).isEqualTo(item.getDescription());
    assertThat(result.getAvailable()).isEqualTo(item.isAvailable());
    assertThat(result.getRequestId()).isNull();
  }

  @Test
  void toDTOWithRequestTest() {
    Item item = Item.builder()
            .id(1L)
            .name("test")
            .description("test")
            .available(Boolean.TRUE)
            .request(ItemRequest.builder()
                    .id(1L)
                    .build())
            .build();

    var result = itemMapper.toItemDto(item);
    assertThat(result.getId()).isEqualTo(item.getId());
    assertThat(result.getName()).isEqualTo(item.getName());
    assertThat(result.getDescription()).isEqualTo(item.getDescription());
    assertThat(result.getAvailable()).isEqualTo(item.isAvailable());
    assertThat(result.getRequestId()).isEqualTo(item.getRequest().getId());
  }

  @Test
  void toFullDTOEmptyFieldsTest() {
    Item item = getDefaultItem();
    List<CommentDto> comments = Collections.emptyList();

    var result = itemMapper.toItemFullDto(item, comments, null, null);

    assertThat(result.getId()).isEqualTo(item.getId());
    assertThat(result.getName()).isEqualTo(item.getName());
    assertThat(result.getDescription()).isEqualTo(item.getDescription());
    assertThat(result.getAvailable()).isEqualTo(item.isAvailable());
    assertThat(result.getLastBooking()).isNull();
    assertThat(result.getNextBooking()).isNull();
    assertThat(result.getComments()).isEmpty();
  }

  @Test
  void toFullDTOAllFieldsTest() {
    Item item = getDefaultItem();
    CommentDto comment = CommentDto.builder()
            .id(1L)
            .text("test")
            .authorName("author")
            .created(LocalDateTime.now())
            .build();
    List<CommentDto> comments = List.of(comment);
    User user = User.builder()
            .id(1L)
            .name("test")
            .email("test@test.ru")
            .build();
    Booking lastBooking = Booking.builder()
            .id(1L)
            .booker(user)
            .build();
    Booking nextBooking = Booking.builder()
            .id(2L)
            .booker(user)
            .build();

    var result = itemMapper.toItemFullDto(item, comments, lastBooking, nextBooking);
    assertThat(result.getId()).isEqualTo(item.getId());
    assertThat(result.getName()).isEqualTo(item.getName());
    assertThat(result.getDescription()).isEqualTo(item.getDescription());
    assertThat(result.getAvailable()).isEqualTo(item.isAvailable());
    assertThat(result.getLastBooking().getId()).isEqualTo(lastBooking.getId());
    assertThat(result.getLastBooking().getBookerId()).isEqualTo(lastBooking.getBooker().getId());
    assertThat(result.getNextBooking().getId()).isEqualTo(nextBooking.getId());
    assertThat(result.getNextBooking().getBookerId()).isEqualTo(nextBooking.getBooker().getId());
    assertThat(result.getComments()).usingRecursiveComparison().isEqualTo(comments);
  }

  private Item getDefaultItem() {
    return Item.builder()
            .id(1L)
            .name("test")
            .description("test")
            .available(Boolean.TRUE)
            .build();
  }
}