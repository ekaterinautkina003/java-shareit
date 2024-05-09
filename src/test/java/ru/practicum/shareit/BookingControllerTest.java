package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.GlobalExceptionHandler;
import ru.practicum.shareit.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@ContextConfiguration(classes = {BookingController.class, GlobalExceptionHandler.class})
public class BookingControllerTest {

  private static final String URL = "http://localhost:8080/bookings";
  @MockBean
  private BookingService bookingService;
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  ObjectMapper mapper;

  @Test
  @SneakyThrows
  void bookNullItemId() {
    var booking = BookingRequestDto.builder()
            .start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now().plusDays(2))
            .build();

    var response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
            .header("Content-Type", "application/json")
            .header("X-Sharer-User-Id", 1L)
            .content(mapper.writeValueAsString(booking)));

    response.andExpect(status().is4xxClientError());
  }

  @Test
  @SneakyThrows
  void bookNullStart() {
    var booking = BookingRequestDto.builder()
            .itemId(1L)
            .end(LocalDateTime.now().plusDays(1))
            .build();

    var response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
            .header("Content-Type", "application/json")
            .header("X-Sharer-User-Id", 1L)
            .content(mapper.writeValueAsString(booking)));

    response.andExpect(status().is4xxClientError());
  }

  @Test
  @SneakyThrows
  void bookNullEnd() {
    var booking = BookingRequestDto.builder()
            .itemId(1L)
            .start(LocalDateTime.now().plusDays(1))
            .build();

    var response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
            .header("Content-Type", "application/json")
            .header("X-Sharer-User-Id", 1L)
            .content(mapper.writeValueAsString(booking)));

    response.andExpect(status().is4xxClientError());
  }

  @Test
  @SneakyThrows
  void bookPastStart() {
    var booking = BookingRequestDto.builder()
            .itemId(1L)
            .start(LocalDateTime.now().minusDays(1))
            .end(LocalDateTime.now().plusDays(1))
            .build();

    var response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
            .header("Content-Type", "application/json")
            .header("X-Sharer-User-Id", 1L)
            .content(mapper.writeValueAsString(booking)));

    response.andExpect(status().is4xxClientError());
  }

  @Test
  @SneakyThrows
  void bookPastEnd() {
    var booking = BookingRequestDto.builder()
            .itemId(1L)
            .start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now().minusDays(1))
            .build();

    var response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
            .header("Content-Type", "application/json")
            .header("X-Sharer-User-Id", 1L)
            .content(mapper.writeValueAsString(booking)));

    response.andExpect(status().is4xxClientError());
  }

  @Test
  @SneakyThrows
  void bookUserNotFound() {
    when(bookingService.create(anyLong(), any()))
            .thenThrow(NotFoundException.class);

    var booking = createBookingRequestDto();

    var response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
            .header("Content-Type", "application/json")
            .header("X-Sharer-User-Id", 1L)
            .content(mapper.writeValueAsString(booking)));

    response.andExpect(status().is4xxClientError());
  }

  @Test
  @SneakyThrows
  void updateBookingNotFound() {
    when(bookingService.update(anyLong(), anyLong(), Mockito.anyBoolean()))
            .thenThrow(NotFoundException.class);

    var response = mockMvc.perform(MockMvcRequestBuilders.patch(URL.concat("/{bookingId}"), 1L)
            .param("approved", Boolean.FALSE.toString())
            .header("Content-Type", "application/json")
            .header("X-Sharer-User-Id", 1L));

    response.andExpect(status().is4xxClientError());
  }

  @Test
  @SneakyThrows
  void getBookingItemNotFound() {
    when(bookingService.getById(anyLong(), anyLong()))
            .thenThrow(NotFoundException.class);

    var response = mockMvc.perform(MockMvcRequestBuilders.get(URL.concat("/{bookingId}"), 1L)
            .header("Content-Type", "application/json")
            .header("X-Sharer-User-Id", 1L));

    response.andExpect(status().is4xxClientError());
  }

  @Test
  @SneakyThrows
  void bookItemNotAvailable() {
    when(bookingService.create(anyLong(), any()))
            .thenThrow(NotFoundException.class);

    var booking = createBookingRequestDto();
    var response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
            .header("Content-Type", "application/json")
            .header("X-Sharer-User-Id", 1L)
            .content(mapper.writeValueAsString(booking)));

    response.andExpect(status().is4xxClientError());
  }

  @Test
  @SneakyThrows
  void booking() {
    BookingRequestDto bookingRequestDTO = createBookingRequestDto();
    var expected = createBookingDto();

    when(bookingService.create(anyLong(), any()))
            .thenReturn(expected);

    var response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
            .header("Content-Type", "application/json")
            .header("X-Sharer-User-Id", 1L)
            .content(mapper.writeValueAsString(bookingRequestDTO)));

    response.andExpect(status().isOk());
  }

  @Test
  @SneakyThrows
  void getById() {
    BookingRequestDto bookingRequestDTO = createBookingRequestDto();
    var expected = createBookingDto();

    when(bookingService.getById(anyLong(), anyLong()))
            .thenReturn(expected);

    var response = mockMvc.perform(MockMvcRequestBuilders.get(URL.concat("/{bookingId}"), 1L)
            .header("Content-Type", "application/json")
            .header("X-Sharer-User-Id", 1L)
            .content(mapper.writeValueAsString(bookingRequestDTO)));

    response.andExpect(status().isOk());
  }

  @Test
  @SneakyThrows
  void getUserBookings() {
    BookingRequestDto bookingRequestDTO = createBookingRequestDto();
    var expected = createBookingDto();

    when(bookingService.getBookingsByUser(anyLong(), any(), any()))
            .thenReturn(List.of(expected));

    var response = mockMvc.perform(MockMvcRequestBuilders.get(URL)
            .header("Content-Type", "application/json")
            .header("X-Sharer-User-Id", 1L)
            .content(mapper.writeValueAsString(bookingRequestDTO)));

    response.andExpect(status().isOk());
  }

  @Test
  @SneakyThrows
  void getUserItemBookings() {
    BookingRequestDto bookingRequestDTO = createBookingRequestDto();
    var expected = createBookingDto();

    when(bookingService.getBookingStatusByOwner(anyLong(), any(), any()))
            .thenReturn(List.of(expected));

    var response = mockMvc.perform(MockMvcRequestBuilders.get(URL.concat("/owner"))
            .header("Content-Type", "application/json")
            .header("X-Sharer-User-Id", 1L)
            .content(mapper.writeValueAsString(bookingRequestDTO)));

    response.andExpect(status().isOk());
  }

  private BookingRequestDto createBookingRequestDto() {
    return BookingRequestDto.builder()
            .itemId(1L)
            .start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now().plusDays(2))
            .build();
  }

  private BookingDto createBookingDto() {
    return BookingDto.builder()
            .id(1L)
            .status("WAITING")
            .start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now().plusDays(2))
            .build();
  }

}
