package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.RequestBookingStatus;
import ru.practicum.shareit.booking.repostiory.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BookingServiceTest {

  @MockBean
  private BookingRepository bookingRepository;
  @MockBean
  private UserRepository userRepository;
  @MockBean
  private ItemRepository itemRepository;
  @Autowired
  private EntityManager entityManager;
  @Autowired
  private BookingService bookingService;

  @Test
  void bookingValidateEqualsDate() {
    var date = LocalDateTime.now();
    BookingRequestDto bookingDto = BookingRequestDto.builder()
            .itemId(1L)
            .start(date)
            .end(date)
            .build();

    assertThrows(ValidationException.class, () -> bookingService.create(1L, bookingDto));
  }

  @Test
  void bookingValidateDate() {
    BookingRequestDto bookingDto = BookingRequestDto.builder()
            .itemId(1L)
            .start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now())
            .build();

    assertThrows(ValidationException.class, () -> bookingService.create(1L, bookingDto));
  }

  @Test
  void bookingUserNotFound() {
    BookingRequestDto bookingRequestDto = getBookingRequestDTO();
    when(userRepository.findById(anyLong()))
            .thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
            () -> bookingService.create(1L, bookingRequestDto));
    verify(userRepository, times(1)).findById(anyLong());
  }

  @Test
  void bookingItemNotFound() {
    BookingRequestDto bookingRequestDto = getBookingRequestDTO();

    when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(createUser()));
    when(itemRepository.findById(anyLong()))
            .thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
            () -> bookingService.create(1L, bookingRequestDto));

    verify(userRepository, times(1)).findById(anyLong());
    verify(itemRepository, times(1)).findById(anyLong());
  }

  @Test
  void bookingSelfItemException() {
    BookingRequestDto bookingRequestDto = getBookingRequestDTO();

    when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(createUser()));
    when(itemRepository.findById(anyLong()))
            .thenReturn(Optional.of(createItem()));

    assertThrows(NotFoundException.class,
            () -> bookingService.create(1L, bookingRequestDto));

    verify(userRepository, times(1)).findById(anyLong());
    verify(itemRepository, times(1)).findById(anyLong());
  }

  @Test
  void bookingItemNotAvalibleException() {
    BookingRequestDto bookingRequestDto = getBookingRequestDTO();

    when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(createUser()));
    when(itemRepository.findById(anyLong()))
            .thenReturn(Optional.of(createItem()));
    when(itemRepository.isItemAvalible(anyLong()))
            .thenReturn(Boolean.FALSE);

    assertThrows(ValidationException.class, () -> bookingService.create(2L, bookingRequestDto));

    verify(userRepository, times(1)).findById(anyLong());
    verify(itemRepository, times(1)).findById(anyLong());
    verify(itemRepository, times(1)).isItemAvalible(anyLong());
  }

  @Test
  void booking() {
    BookingRequestDto bookingRequestDto = getBookingRequestDTO();
    when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(createUser()));
    when(itemRepository.findById(anyLong()))
            .thenReturn(Optional.of(createItem()));
    when(itemRepository.isItemAvalible(anyLong()))
            .thenReturn(Boolean.TRUE);
    when(bookingRepository.save(any()))
            .thenReturn(createBooking());

    var result = bookingService.create(2L, bookingRequestDto);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(2L);
    assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING.name());
    assertThat(result.getStart()).isEqualTo(bookingRequestDto.getStart());
    assertThat(result.getEnd()).isEqualTo(bookingRequestDto.getEnd());
    assertThat(result.getItem()).isNotNull();
    assertThat(result.getBooker()).isNotNull();

    verify(userRepository, times(1)).findById(anyLong());
    verify(itemRepository, times(1)).findById(anyLong());
    verify(itemRepository, times(1)).isItemAvalible(anyLong());
    verify(bookingRepository, times(1)).save(any());
  }

  @Test
  void updateBookingUserNotFoundException() {
    when(userRepository.findById(anyLong()))
            .thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
            () -> bookingService.update(1L, 1L, Boolean.TRUE));
    verify(userRepository, times(1)).findById(anyLong());
  }

  @Test
  void updateBookingBookingNotFoundException() {
    when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(createUser()));
    when(bookingRepository.findById(anyLong()))
            .thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
            () -> bookingService.update(1L, 1L, Boolean.TRUE));

    verify(userRepository, times(1)).findById(anyLong());
    verify(bookingRepository, times(1)).findById(anyLong());
  }

  @Test
  void updateBookingStatusException() {
    Booking booking = createBooking();
    booking.setStatus(BookingStatus.APPROVED.name());
    when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(createUser()));
    when(bookingRepository.findById(anyLong()))
            .thenReturn(Optional.of(booking));

    assertThrows(ValidationException.class,
            () -> bookingService.update(1L, 1L, Boolean.TRUE));
    verify(userRepository, times(1)).findById(anyLong());
    verify(bookingRepository, times(1)).findById(anyLong());
  }

  @Test
  void updateBookingItemException() {
    Booking booking = createBooking();
    when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(createUser()));
    when(bookingRepository.findById(anyLong()))
            .thenReturn(Optional.of(booking));
    when(itemRepository.findById(anyLong()))
            .thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
            () -> bookingService.update(1L, 1L, Boolean.TRUE));
    verify(userRepository, times(1)).findById(anyLong());
    verify(bookingRepository, times(1)).findById(anyLong());
    verify(itemRepository, times(1)).findById(anyLong());
  }

  @Test
  void updateBookingOwnerException() {
    Booking booking = createBooking();

    when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(createUser()));
    when(bookingRepository.findById(anyLong()))
            .thenReturn(Optional.of(booking));
    when(itemRepository.findById(anyLong()))
            .thenReturn(Optional.of(createItem()));

    assertThrows(NotFoundException.class,
            () -> bookingService.update(2L, 1L, Boolean.TRUE));
    verify(userRepository, times(1)).findById(anyLong());
    verify(bookingRepository, times(1)).findById(anyLong());
    verify(itemRepository, times(1)).findById(anyLong());
  }

  @Test
  void getBookingUserException() {
    when(userRepository.findById(anyLong()))
            .thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> bookingService.getById(1L, 1L));
    verify(userRepository, times(1)).findById(anyLong());
  }

  @Test
  void getBookingBookingNotFoundException() {
    when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(createUser()));
    when(bookingRepository.findById(anyLong()))
            .thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> bookingService.getById(1L, 1L));
    verify(userRepository, times(1)).findById(anyLong());
    verify(bookingRepository, times(1)).findById(anyLong());
  }

  @Test
  void getBookingItemNotFoundException() {
    when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(createUser()));
    when(bookingRepository.findById(anyLong()))
            .thenReturn(Optional.of(createBooking()));
    when(itemRepository.findById(anyLong()))
            .thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> bookingService.getById(1L, 1L));
    verify(userRepository, times(1)).findById(anyLong());
    verify(bookingRepository, times(1)).findById(anyLong());
    verify(itemRepository, times(1)).findById(anyLong());
  }

  @Test
  void getBookingUserNotFoundException() {
    when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(createUser()));
    when(bookingRepository.findById(anyLong()))
            .thenReturn(Optional.of(createBooking()));
    when(itemRepository.findById(anyLong()))
            .thenReturn(Optional.of(createItem()));

    assertThrows(NotFoundException.class, () -> bookingService.getById(2L, 1L));
    verify(userRepository, times(1)).findById(anyLong());
    verify(bookingRepository, times(1)).findById(anyLong());
    verify(itemRepository, times(1)).findById(anyLong());
  }

  @Test
  void getBooking() {
    Booking booking = createBooking();
    when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(createUser()));
    when(bookingRepository.findById(anyLong()))
            .thenReturn(Optional.of(booking));
    when(itemRepository.findById(anyLong()))
            .thenReturn(Optional.of(createItem()));

    var result = bookingService.getById(1L, 1L);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(booking.getId());
    assertThat(result.getStatus()).isEqualTo(booking.getStatus());
    assertThat(result.getStart()).isEqualTo(booking.getStart());
    assertThat(result.getEnd()).isEqualTo(booking.getEnd());
    assertThat(result.getItem()).isNotNull();
    assertThat(result.getBooker()).isNotNull();

    verify(userRepository, times(1)).findById(anyLong());
    verify(bookingRepository, times(1)).findById(anyLong());
    verify(itemRepository, times(1)).findById(anyLong());
  }

  @Test
  void getBookingsByUserException() {
    when(userRepository.findById(anyLong()))
            .thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
            () -> bookingService.getBookingsByUser(1L, RequestBookingStatus.ALL.name(), null));
    verify(userRepository, times(1)).findById(anyLong());
  }

  @Test
  void getBookingsByUserAll() {
    Booking booking = createBooking();
    when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(createUser()));
    when(bookingRepository.findBookingByBookerIdOrderByStartDesc(anyLong(), any()))
            .thenReturn(List.of(booking));

    var result = bookingService.getBookingsByUser(1L, RequestBookingStatus.ALL.name(), null);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(booking.getId());
    assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
    assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
    assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
    assertThat(result.get(0).getItem()).isNotNull();
    assertThat(result.get(0).getBooker()).isNotNull();
    verify(userRepository, times(1)).findById(anyLong());
    verify(bookingRepository, times(1)).findBookingByBookerIdOrderByStartDesc(anyLong(), any());
  }

  @Test
  void getBookingsByUserWaiting() {
    Booking booking = createBooking();
    when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(createUser()));
    when(bookingRepository.findBookingByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any()))
            .thenReturn(List.of(booking));

    var result = bookingService.getBookingsByUser(1L, RequestBookingStatus.WAITING.name(), null);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(booking.getId());
    assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
    assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
    assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
    assertThat(result.get(0).getItem()).isNotNull();
    assertThat(result.get(0).getBooker()).isNotNull();
    verify(userRepository, times(1)).findById(anyLong());
    verify(bookingRepository, times(1)).findBookingByBookerIdAndStatusOrderByStartDesc(anyLong(),
            any(), any());
  }

  @Test
  void getBookingsByUserRejected() {
    Booking booking = createBooking();
    when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(createUser()));
    when(bookingRepository.findBookingByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any()))
            .thenReturn(List.of(booking));

    var result = bookingService.getBookingsByUser(1L, RequestBookingStatus.REJECTED.name(), null);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(booking.getId());
    assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
    assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
    assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
    assertThat(result.get(0).getItem()).isNotNull();
    assertThat(result.get(0).getBooker()).isNotNull();
    verify(userRepository, times(1)).findById(anyLong());
    verify(bookingRepository, times(1)).findBookingByBookerIdAndStatusOrderByStartDesc(anyLong(),
            any(), any());
  }

  @Test
  void getBookingsByUserCurrent() {
    Booking booking = createBooking();
    when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(createUser()));
    when(bookingRepository.findCurrentBookingByBookerId(anyLong(), any()))
            .thenReturn(List.of(booking));

    var result = bookingService.getBookingsByUser(1L, RequestBookingStatus.CURRENT.name(), null);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(booking.getId());
    assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
    assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
    assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
    assertThat(result.get(0).getItem()).isNotNull();
    assertThat(result.get(0).getBooker()).isNotNull();
    verify(userRepository, times(1)).findById(anyLong());
    verify(bookingRepository, times(1)).findCurrentBookingByBookerId(anyLong(), any());
  }

  @Test
  void getBookingsByUserPast() {
    Booking booking = createBooking();
    when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(createUser()));
    when(bookingRepository.findPastBookingByBookerId(anyLong(), any()))
            .thenReturn(List.of(booking));

    var result = bookingService.getBookingsByUser(1L, RequestBookingStatus.PAST.name(), null);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(booking.getId());
    assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
    assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
    assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
    assertThat(result.get(0).getItem()).isNotNull();
    assertThat(result.get(0).getBooker()).isNotNull();
    verify(userRepository, times(1)).findById(anyLong());
    verify(bookingRepository, times(1)).findPastBookingByBookerId(anyLong(), any());
  }

  @Test
  void getBookingsByUserFuture() {
    Booking booking = createBooking();
    when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(createUser()));
    when(bookingRepository.findFutureBookingByBookerId(anyLong(), any()))
            .thenReturn(List.of(booking));

    var result = bookingService.getBookingsByUser(1L, RequestBookingStatus.FUTURE.name(), null);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(booking.getId());
    assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
    assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
    assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
    assertThat(result.get(0).getItem()).isNotNull();
    assertThat(result.get(0).getBooker()).isNotNull();
    verify(userRepository, times(1)).findById(anyLong());
    verify(bookingRepository, times(1)).findFutureBookingByBookerId(anyLong(), any());
  }

  @Test
  void getBookingStatusByOwnerException() {
    when(userRepository.findById(anyLong()))
            .thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
            () -> bookingService.getBookingStatusByOwner(1L, RequestBookingStatus.ALL.name(), null));
    verify(userRepository, times(1)).findById(anyLong());
  }

  @Test
  void getBookingStatusByOwnerItemException() {
    when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(createUser()));

    bookingService.getBookingStatusByOwner(1L, RequestBookingStatus.ALL.name(), null);

    verify(userRepository, times(1)).findById(anyLong());
  }

  @Test
  void getBookingStatusByOwnerAll() {
    Booking booking = createBooking();
    when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(createUser()));
    when(itemRepository.getAllByOwnerId(anyLong()))
            .thenReturn(List.of(createItem()));
    when(bookingRepository.findBookingsByItem_OwnerIdOrderByStartDesc(anyLong(), any()))
            .thenReturn(List.of(booking));

    var result = bookingService.getBookingStatusByOwner(1L, RequestBookingStatus.ALL.name(), null);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(booking.getId());
    assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
    assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
    assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
    assertThat(result.get(0).getItem()).isNotNull();
    assertThat(result.get(0).getBooker()).isNotNull();
    verify(userRepository, times(1)).findById(anyLong());
    verify(bookingRepository, times(1)).findBookingsByItem_OwnerIdOrderByStartDesc(anyLong(),
            any());
  }

  @Test
  void getBookingStatusByOwnerWaiting() {
    Booking booking = createBooking();
    when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(createUser()));
    when(itemRepository.getAllByOwnerId(anyLong()))
            .thenReturn(List.of(createItem()));
    when(bookingRepository.findBookingsByItem_OwnerIdAndStatusOrderByStartDesc(anyLong(), any(),
            any()))
            .thenReturn(List.of(booking));

    var result = bookingService.getBookingStatusByOwner(1L, RequestBookingStatus.WAITING.name(), null);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(booking.getId());
    assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
    assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
    assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
    assertThat(result.get(0).getItem()).isNotNull();
    assertThat(result.get(0).getBooker()).isNotNull();
    verify(userRepository, times(1)).findById(anyLong());
    verify(bookingRepository, times(1)).findBookingsByItem_OwnerIdAndStatusOrderByStartDesc(
            anyLong(), any(), any());
  }

  @Test
  void getBookingStatusByOwnerRejected() {
    Booking booking = createBooking();
    when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(createUser()));
    when(itemRepository.getAllByOwnerId(anyLong()))
            .thenReturn(List.of(createItem()));
    when(bookingRepository.findBookingsByItem_OwnerIdAndStatusOrderByStartDesc(anyLong(), any(),
            any()))
            .thenReturn(List.of(booking));

    var result = bookingService.getBookingStatusByOwner(1L, RequestBookingStatus.REJECTED.name(), null);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(booking.getId());
    assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
    assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
    assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
    assertThat(result.get(0).getItem()).isNotNull();
    assertThat(result.get(0).getBooker()).isNotNull();
    verify(userRepository, times(1)).findById(anyLong());
  }

  @Test
  void getBookingStatusByOwnerCurrent() {
    Booking booking = createBooking();
    when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(createUser()));
    when(bookingRepository.findCurrentBookingByOwnerId(anyLong(), any()))
            .thenReturn(List.of(booking));

    var result = bookingService.getBookingStatusByOwner(1L, RequestBookingStatus.CURRENT.name(), null);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(booking.getId());
    assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
    assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
    assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
    assertThat(result.get(0).getItem()).isNotNull();
    assertThat(result.get(0).getBooker()).isNotNull();
    verify(userRepository, times(1)).findById(anyLong());
  }

  @Test
  void getBookingStatusByOwnerPast() {
    Booking booking = createBooking();
    when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(createUser()));
    when(itemRepository.getAllByOwnerId(anyLong()))
            .thenReturn(List.of(createItem()));
    when(bookingRepository.findBookingsByItem_OwnerIdAndEndBeforeOrderByStartDesc(anyLong(), any(),
            any()))
            .thenReturn(List.of(booking));

    var result = bookingService.getBookingStatusByOwner(1L, RequestBookingStatus.PAST.name(), null);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(booking.getId());
    assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
    assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
    assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
    assertThat(result.get(0).getItem()).isNotNull();
    assertThat(result.get(0).getBooker()).isNotNull();
    verify(userRepository, times(1)).findById(anyLong());
    verify(bookingRepository, times(1)).findBookingsByItem_OwnerIdAndEndBeforeOrderByStartDesc(
            anyLong(), any(), any());
  }

  @Test
  void getBookingStatusByOwnerFuture() {
    Booking booking = createBooking();

    when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(createUser()));
    when(itemRepository.getAllByOwnerId(anyLong()))
            .thenReturn(List.of(createItem()));
    when(bookingRepository.findBookingsByItem_OwnerIdAndStartAfterOrderByStartDesc(anyLong(), any(),
            any()))
            .thenReturn(List.of(booking));

    var result = bookingService.getBookingStatusByOwner(1L, RequestBookingStatus.FUTURE.name(), null);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(booking.getId());
    assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
    assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
    assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
    assertThat(result.get(0).getItem()).isNotNull();
    assertThat(result.get(0).getBooker()).isNotNull();

    verify(userRepository, times(1)).findById(anyLong());
    verify(bookingRepository, times(1)).findBookingsByItem_OwnerIdAndStartAfterOrderByStartDesc(
            anyLong(), any(), any());
  }

  private BookingRequestDto getBookingRequestDTO() {
    return BookingRequestDto.builder()
            .itemId(1L)
            .start(LocalDateTime.parse("2030-01-01T00:00:00"))
            .end(LocalDateTime.parse("2030-02-01T00:00:00"))
            .build();
  }

  private User createUser() {
    return User.builder()
            .id(1L)
            .name("name")
            .email("test@test.ru")
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

  private Booking createBooking() {
    return Booking.builder()
            .id(2L)
            .start(LocalDateTime.parse("2030-01-01T00:00:00"))
            .end(LocalDateTime.parse("2030-02-01T00:00:00"))
            .item(createItem())
            .booker(createUser())
            .status(BookingStatus.WAITING.name())
            .build();
  }
}