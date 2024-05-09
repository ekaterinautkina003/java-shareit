package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ItemRequestServiceTest {

  @MockBean
  private UserRepository userRepository;
  @MockBean
  private ItemRepository itemRepository;
  @MockBean
  private ItemRequestRepository itemRequestRepository;
  @Autowired
  private ItemRequestService itemRequestService;

  @Test
  public void createUserNotFound() {
    ItemRequestShortDto request = createItemRequestShortDto();
    when(userRepository.findById(anyLong()))
            .thenThrow(NotFoundException.class);

    assertThrows(NotFoundException.class, () -> itemRequestService.create(1L, request));

    verify(userRepository, times(1)).findById(any());
  }

  @Test
  public void create() {
    ItemRequest itemRequest = createItemRequest();
    when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(createUser()));
    when(itemRequestRepository.save(any()))
            .thenReturn(itemRequest);

    var result = itemRequestService.create(1L, createItemRequestShortDto());

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(itemRequest.getId());
    assertThat(result.getDescription()).isEqualTo(itemRequest.getDescription());
    assertThat(result.getCreated()).isEqualTo(itemRequest.getCreated());
    verify(userRepository, times(1)).findById(any());
    verify(itemRequestRepository, times(1)).save(any());
  }

  @Test
  public void getByIdItemNotFound() {
    when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(createUser()));
    when(itemRequestRepository.findById(anyLong()))
            .thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> itemRequestService.get(1L, 1L));

    verify(userRepository, times(1)).findById(any());
    verify(itemRequestRepository, times(1)).findById(any());
  }

  @Test
  public void getById() {
    ItemRequest itemRequest = createItemRequest();
    when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(createUser()));
    when(itemRequestRepository.findById(anyLong()))
            .thenReturn(Optional.of(itemRequest));
    when(itemRepository.getAllByRequestId(anyLong()))
            .thenReturn(Collections.emptyList());

    var result = itemRequestService.get(1L, 1L);
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(itemRequest.getId());
    assertThat(result.getDescription()).isEqualTo(itemRequest.getDescription());
    assertThat(result.getCreated()).isEqualTo(itemRequest.getCreated());
    assertThat(result.getItems()).isEmpty();

    verify(userRepository, times(1)).findById(any());
    verify(itemRequestRepository, times(1)).findById(any());
  }

  @Test
  public void getAll() {
    ItemRequest itemRequest = createItemRequest();
    when(itemRequestRepository.findAllWithoutUserId(anyLong(), any()))
            .thenReturn(List.of(itemRequest));

    var result = itemRequestService.getAll(1L, PageRequest.ofSize(1));

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(itemRequest.getId());
    assertThat(result.get(0).getDescription()).isEqualTo(itemRequest.getDescription());
    assertThat(result.get(0).getCreated()).isEqualTo(itemRequest.getCreated());
    assertThat(result.get(0).getItems()).isEmpty();
    verify(itemRequestRepository, times(1)).findAllWithoutUserId(anyLong(), any());
  }


  @Test
  public void getAllSelfRequests() {
    ItemRequest itemRequest = createItemRequest();
    when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(createUser()));
    when(itemRequestRepository.findAllByRequestorId(anyLong()))
            .thenReturn(List.of(itemRequest));

    var result = itemRequestService.getSelf(1L);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(itemRequest.getId());
    assertThat(result.get(0).getDescription()).isEqualTo(itemRequest.getDescription());
    assertThat(result.get(0).getCreated()).isEqualTo(itemRequest.getCreated());
    assertThat(result.get(0).getItems()).isEmpty();
    verify(userRepository, times(1)).findById(any());
    verify(itemRequestRepository, times(1)).findAllByRequestorId(anyLong());
  }

  private ItemRequestShortDto createItemRequestShortDto() {
    ItemRequestShortDto itemRequestShortDto = new ItemRequestShortDto();
    itemRequestShortDto.setDescription("test");
    return itemRequestShortDto;
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

}