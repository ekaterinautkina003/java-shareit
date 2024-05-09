package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repostiory.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ItemServiceTest {

  @MockBean
  private ItemRepository itemRepository;
  @MockBean
  private UserRepository userRepository;
  @MockBean
  private CommentRepository commentRepository;
  @MockBean
  private BookingRepository bookingRepository;
  @MockBean
  private ItemRequestRepository itemRequestRepository;
  @Autowired
  private ItemService itemService;

  @Test
  void addUserException() {
    ItemDto itemDto = createItemDto();

    when(userRepository.findById(anyLong()))
            .thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> itemService.create(1L, itemDto));
    verify(userRepository, times(1)).findById(any());
  }

  @Test
  void addWithRequestId() {
    ItemDto itemDTO = createItemDto();
    itemDTO.setRequestId(1L);
    User user = createUser();
    Item item = createItem();
    ItemRequest itemRequest = createItemRequest();
    item.setRequest(itemRequest);

    when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(user));
    when(itemRequestRepository.findById(anyLong()))
            .thenReturn(Optional.of(itemRequest));
    when(itemRepository.save(any()))
            .thenReturn(item);

    var result = itemService.create(1L, itemDTO);
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(item.getId());
    assertThat(result.getName()).isEqualTo(item.getName());
    assertThat(result.getDescription()).isEqualTo(item.getDescription());
    assertThat(result.getAvailable()).isEqualTo(item.isAvailable());
    assertThat(result.getRequestId()).isEqualTo(item.getRequest().getId());

    verify(userRepository, times(1)).findById(any());
    verify(itemRequestRepository, times(1)).findById(any());
    verify(itemRepository, times(1)).save(any());
  }

  @Test
  void addWithoutRequestId() {
    ItemDto itemDTO = createItemDto();
    User user = createUser();
    Item item = createItem();

    when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(user));
    when(itemRepository.save(any()))
            .thenReturn(item);

    var result = itemService.create(1L, itemDTO);
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(item.getId());
    assertThat(result.getName()).isEqualTo(item.getName());
    assertThat(result.getDescription()).isEqualTo(item.getDescription());
    assertThat(result.getAvailable()).isEqualTo(item.isAvailable());

    verify(userRepository, times(1)).findById(any());
    verify(itemRepository, times(1)).save(any());
  }

  @Test
  void editUserException() {
    ItemDto itemDTO = createItemDto();

    when(itemRepository.findById(anyLong()))
            .thenReturn(Optional.of(createItem()));

    assertThrows(NotFoundException.class, () -> itemService.update(2L, 1L, itemDTO));
    verify(itemRepository, times(1)).findById(any());
  }

  @Test
  void edit() {
    ItemDto itemDto = createItemDto();

    when(itemRepository.findById(anyLong()))
            .thenReturn(Optional.ofNullable(createItem()));
    when(itemRepository.save(any()))
            .thenReturn(createItem());

    var result = itemService.update(1L, 1L, itemDto);
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(itemDto.getId());
    assertThat(result.getName()).isEqualTo(itemDto.getName());
    assertThat(result.getDescription()).isEqualTo(itemDto.getDescription());
    assertThat(result.getAvailable()).isEqualTo(itemDto.getAvailable());

    verify(itemRepository, times(1)).findById(any());
    verify(itemRepository, times(1)).save(any());
  }

  @Test
  void getByIdNotFoundException() {
    when(itemRepository.findById(anyLong()))
            .thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> itemService.getById(1L, 1L));
    verify(itemRepository, times(1)).findById(anyLong());

  }

  @Test
  void getById() {
    ItemDto itemDTO = createItemDto();
    Booking booking = createBooking();
    Comment comment = createComment();

    when(itemRepository.findById(anyLong()))
            .thenReturn(Optional.of(createItem()));
    when(bookingRepository.findBookingsByItemId(anyLong()))
            .thenReturn(List.of(booking));
    when(commentRepository.findAllByItemId(anyLong()))
            .thenReturn(List.of(comment));

    var result = itemService.getById(1L, 1L);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(itemDTO.getId());
    assertThat(result.getName()).isEqualTo(itemDTO.getName());
    assertThat(result.getDescription()).isEqualTo(itemDTO.getDescription());
    assertThat(result.getAvailable()).isEqualTo(itemDTO.getAvailable());
    assertThat(result.getNextBooking()).isNull();
    assertThat(result.getLastBooking()).isNotNull();
    assertThat(result.getComments()).hasSize(1);
    assertThat(result.getComments().get(0).getId()).isEqualTo(comment.getId());
    assertThat(result.getComments().get(0).getText()).isEqualTo(comment.getText());
    assertThat(result.getComments().get(0).getAuthorName()).isEqualTo(comment.getUser().getName());

    verify(itemRepository, times(1)).findById(anyLong());
    verify(bookingRepository, times(1)).findBookingsByItemId(anyLong());
    verify(commentRepository, times(1)).findAllByItemId(anyLong());
  }

  @Test
  void getByIdAllBookingDate() {
    ItemDto itemDTO = createItemDto();
    Booking booking1 = createBooking();
    booking1.setStart(LocalDateTime.now().minusDays(2));
    booking1.setEnd(LocalDateTime.now().minusDays(1));
    Booking booking2 = createBooking();
    Comment comment = createComment();

    when(itemRepository.findById(anyLong()))
            .thenReturn(Optional.of(createItem()));
    when(bookingRepository.findBookingsByItemId(anyLong()))
            .thenReturn(List.of(booking1, booking2));
    when(commentRepository.findAllByItemId(anyLong()))
            .thenReturn(List.of(comment));

    var result = itemService.getById(2L, 1L);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(itemDTO.getId());
    assertThat(result.getName()).isEqualTo(itemDTO.getName());
    assertThat(result.getDescription()).isEqualTo(itemDTO.getDescription());
    assertThat(result.getAvailable()).isEqualTo(itemDTO.getAvailable());
    assertThat(result.getNextBooking()).isNotNull();
    assertThat(result.getLastBooking()).isNotNull();
    assertThat(result.getComments()).hasSize(1);
    assertThat(result.getComments().get(0).getId()).isEqualTo(comment.getId());
    assertThat(result.getComments().get(0).getText()).isEqualTo(comment.getText());
    assertThat(result.getComments().get(0).getAuthorName()).isEqualTo(comment.getUser().getName());

    verify(itemRepository, times(1)).findById(anyLong());
    verify(bookingRepository, times(1)).findBookingsByItemId(anyLong());
    verify(commentRepository, times(1)).findAllByItemId(anyLong());
  }

  @Test
  void searchEmptyText() {
    var result = itemService.searchByText("", null);
    assertThat(result).isEmpty();
  }

  @Test
  void search() {
    Item item = createItem();
    when(itemRepository.searchByText(any(), any()))
            .thenReturn(List.of(item));

    var result = itemService.searchByText("test", null);
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo(item.getName());
    assertThat(result.get(0).getDescription()).isEqualTo(item.getDescription());
    assertThat(result.get(0).getAvailable()).isEqualTo(item.isAvailable());

    verify(itemRepository, times(1)).searchByText(any(), any());
  }

  @Test
  void commentBookingException() {
    when(bookingRepository.existsBookingByBookerIdAndStatus(anyLong(), any()))
            .thenReturn(Boolean.FALSE);
    when(itemRepository.findById(anyLong()))
            .thenReturn(Optional.of(createItem()));
    when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(createUser()));

    assertThrows(ValidationException.class,
            () -> itemService.addCommentToItem(1L, 1L, CommentRequestDto.builder()
                    .text("test")
                    .build()));
  }

  @Test
  void comment() {
    when(bookingRepository.existsBookingByBookerIdAndStatus(anyLong(), any()))
            .thenReturn(Boolean.TRUE);
    when(itemRepository.findById(anyLong()))
            .thenReturn(Optional.of(createItem()));
    when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(createUser()));
    when(commentRepository.save(any()))
            .thenReturn(createComment());

    var result = itemService.addCommentToItem(1L, 1L, CommentRequestDto.builder()
            .text("test")
            .build());

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getText()).isEqualTo("test");
    assertThat(result.getAuthorName()).isEqualTo("name");

    verify(itemRepository, times(1)).findById(anyLong());
    verify(userRepository, times(1)).findById(anyLong());
    verify(commentRepository, times(1)).save(any());
    verify(bookingRepository, times(1)).existsBookingByBookerIdAndStatus(anyLong(), any());
  }

  private Comment createComment() {
    return Comment.builder()
            .id(1L)
            .text("test")
            .item(createItem())
            .user(createUser())
            .build();
  }

  private Booking createBooking() {
    return Booking.builder()
            .id(1L)
            .start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now().plusDays(2))
            .item(createItem())
            .booker(createUser())
            .status(BookingStatus.APPROVED.name())
            .build();
  }

  private ItemDto createItemDto() {
    return ItemDto.builder()
            .id(1L)
            .name("test")
            .description("test")
            .available(Boolean.TRUE)
            .build();
  }

  private User createUser() {
    return User.builder()
            .id(1L)
            .name("name")
            .email("test@test.ru")
            .build();
  }

  private ItemRequest createItemRequest() {
    return ItemRequest.builder()
            .id(1L)
            .description("test")
            .requestor(createUser())
            .build();
  }

  private Item createItem() {
    return Item.builder()
            .id(1L)
            .name("test")
            .description("test")
            .owner(createUser())
            .available(Boolean.TRUE)
            .build();
  }
}
