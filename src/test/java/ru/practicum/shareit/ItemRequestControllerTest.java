package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.exception.GlobalExceptionHandler;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@ContextConfiguration(classes = {ItemRequestController.class, GlobalExceptionHandler.class})
public class ItemRequestControllerTest {

  private static final String URL = "http://localhost:8080/requests";
  @MockBean
  private ItemRequestService itemRequestService;
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper mapper;

  @Test
  @SneakyThrows
  void createEmptyDescription() {
    var response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
            .header("Content-Type", "application/json")
            .header("X-Sharer-User-Id", 1L)
            .content(mapper.writeValueAsString(ItemRequestDto.builder().build())));

    response.andExpect(status().is4xxClientError());
  }

  @Test
  @SneakyThrows
  void createUserNotFound() {
    when(itemRequestService.create(anyLong(), any()))
            .thenThrow(NotFoundException.class);

    var itemRequestDTO = createItemRequestDto();
    var response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
            .header("Content-Type", "application/json")
            .header("X-Sharer-User-Id", 1L)
            .content(mapper.writeValueAsString(itemRequestDTO)));

    response.andExpect(status().is4xxClientError());
  }

  @Test
  @SneakyThrows
  void getByIdRequestNotFound() {
    when(itemRequestService.get(anyLong(), anyLong()))
            .thenThrow(NotFoundException.class);

    var response = mockMvc.perform(MockMvcRequestBuilders.get(URL.concat("/{requestId}"), 1L)
            .header("X-Sharer-User-Id", 1L));

    response.andExpect(status().is4xxClientError());
  }

  @Test
  @SneakyThrows
  void create() {
    ItemRequestDto itemRequestDTO = createItemRequestDto();
    when(itemRequestService.create(anyLong(), any()))
            .thenReturn(itemRequestDTO);

    var response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
            .header("Content-Type", "application/json")
            .header("X-Sharer-User-Id", 1L)
            .content(mapper.writeValueAsString(itemRequestDTO)));

    response.andExpect(status().isOk());
  }

  private ItemRequestDto createItemRequestDto() {
    return ItemRequestDto.builder()
            .description("test")
            .build();
  }
}