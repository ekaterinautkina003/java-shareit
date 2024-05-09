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
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@ContextConfiguration(classes = {ItemController.class, GlobalExceptionHandler.class})
public class ItemControllerTest {

  private static final String URL = "http://localhost:8080/items";
  @MockBean
  private ItemService itemService;
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  ObjectMapper mapper;

  @Test
  @SneakyThrows
  void addEmptyName() {
    ItemDto item = ItemDto.builder()
            .id(1L)
            .description("test")
            .available(Boolean.FALSE)
            .build();

    var response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
            .header("Content-Type", "application/json")
            .header("X-Sharer-User-Id", 1L)
            .content(mapper.writeValueAsString(item)));

    response.andExpect(status().is4xxClientError());
  }

  @Test
  @SneakyThrows
  void addEmptyDescription() {
    ItemDto item = ItemDto.builder()
            .id(1L)
            .name("test")
            .available(Boolean.TRUE)
            .build();

    var response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
            .header("Content-Type", "application/json")
            .header("X-Sharer-User-Id", 1L)
            .content(mapper.writeValueAsString(item)));

    response.andExpect(status().is4xxClientError());
  }

  @Test
  @SneakyThrows
  void addNullAvailable() {
    ItemDto item = ItemDto.builder()
            .id(1L)
            .name("test")
            .description("test")
            .build();

    var response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
            .header("Content-Type", "application/json")
            .header("X-Sharer-User-Id", 1L)
            .content(mapper.writeValueAsString(item)));

    response.andExpect(status().is4xxClientError());
  }

  @Test
  @SneakyThrows
  void addUserNotFound() {
    var item = createItemDto();
    when(itemService.create(anyLong(), any()))
            .thenThrow(NotFoundException.class);

    var response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
            .header("Content-Type", "application/json")
            .header("X-Sharer-User-Id", 1L)
            .content(mapper.writeValueAsString(item)));

    response.andExpect(status().is4xxClientError());
  }

  @Test
  @SneakyThrows
  void getByIdNotFound() {
    var item = createItemDto();
    when(itemService.getById(anyLong(), anyLong()))
            .thenThrow(NotFoundException.class);

    var response = mockMvc.perform(MockMvcRequestBuilders.get(URL.concat("/{itemId}"), 1L)
            .header("X-Sharer-User-Id", 1L));

    response.andExpect(status().is4xxClientError());
  }

  @Test
  @SneakyThrows
  void add() {
    var item = createItemDto();
    when(itemService.create(anyLong(), any()))
            .thenReturn(item);

    var response = mockMvc.perform(MockMvcRequestBuilders.post(URL)
            .header("X-Sharer-User-Id", 1L)
            .header("Content-Type", "application/json")
            .content(mapper.writeValueAsString(item)));

    response.andExpect(status().isOk());
  }

  @Test
  @SneakyThrows
  void getById() {
    var item = ItemFullDto.builder()
            .id(1L)
            .name("test")
            .description("test")
            .available(Boolean.TRUE)
            .comments(Collections.emptyList())
            .lastBooking(null)
            .nextBooking(null)
            .build();
    when(itemService.getById(anyLong(), anyLong()))
            .thenReturn(item);

    var response = mockMvc.perform(MockMvcRequestBuilders.get(URL.concat("/{itemId}"), 1L)
            .header("X-Sharer-User-Id", 1L));

    response.andExpect(status().isOk());
  }

  @Test
  @SneakyThrows
  void getUsersItems() {
    var item = ItemFullDto.builder()
            .id(1L)
            .name("test")
            .description("test")
            .available(Boolean.TRUE)
            .comments(Collections.emptyList())
            .lastBooking(null)
            .nextBooking(null)
            .build();
    when(itemService.getUserItems(anyLong(), any()))
            .thenReturn(List.of(item));

    var response = mockMvc.perform(MockMvcRequestBuilders.get(URL)
            .header("X-Sharer-User-Id", 1L));

    response.andExpect(status().isOk());
  }

  @Test
  @SneakyThrows
  void search() {
    var item = createItemDto();
    when(itemService.searchByText(any(), any()))
            .thenReturn(List.of(item));

    var response = mockMvc.perform(MockMvcRequestBuilders.get(URL.concat("/search"))
            .param("text", "test")
            .header("X-Sharer-User-Id", 1L));

    response.andExpect(status().isOk());
  }

  private ItemDto createItemDto() {
    return ItemDto.builder()
            .id(1L)
            .name("test")
            .description("test")
            .available(Boolean.TRUE)
            .build();
  }
}
