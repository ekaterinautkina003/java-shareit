package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repostiory.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

  private final ItemMapper itemMapper;
  private final UserRepository userRepository;
  private final CommentMapper commentMapper;
  private final ItemRepository itemRepository;
  private final BookingRepository bookingRepository;
  private final CommentRepository commentRepository;
  private final ItemRequestRepository itemRequestRepository;

  @Override
  public ItemDto create(Long userId, ItemDto itemDto) {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      throw new NotFoundException();
    }
    if (itemDto.getRequestId() != null) {
      return itemRequestRepository.findById(itemDto.getRequestId())
              .map(item -> itemMapper.toItemWithRequest(itemDto, user.get(), item))
              .map(itemRepository::save)
              .map(itemMapper::toItemDto)
              .orElseThrow(NotFoundException::new);
    }
    Item item = itemMapper.toItem(itemDto, user.get());
    return itemMapper.toItemDto(itemRepository.save(item));
  }

  @Override
  public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
    Item result = itemRepository.findById(itemId).get();
    if (!Objects.equals(userId, result.getOwner().getId())) {
      throw new NotFoundException();
    }
    if (Objects.nonNull(itemDto.getName())) {
      result.setName(itemDto.getName());
    }
    if (Objects.nonNull(itemDto.getDescription())) {
      result.setDescription(itemDto.getDescription());
    }
    if (Objects.nonNull(itemDto.getAvailable())) {
      result.setAvailable(itemDto.getAvailable());
    }

    return itemMapper.toItemDto(itemRepository.save(result));
  }

  @Override
  public List<ItemDto> searchByText(String text, Pageable pageable) {
    if (text.isEmpty()) {
      return Collections.emptyList();
    }
    return itemRepository.searchByText(text, pageable)
            .stream()
            .map(itemMapper::toItemDto)
            .collect(Collectors.toList());
  }

  @Override
  public CommentDto addCommentToItem(Long itemId, Long userId, CommentRequestDto commentDto) {
    User user = userRepository.findById(userId)
            .orElseThrow(NotFoundException::new);
    Item item = itemRepository.findById(itemId)
            .orElseThrow(NotFoundException::new);
    if (!bookingRepository.existsBookingByBookerIdAndStatus(userId, BookingStatus.APPROVED.name())) {
      throw new ValidationException();
    }
    Comment comment = commentRepository.save(Comment.builder()
            .text(commentDto.getText())
            .item(item)
            .user(user)
            .build());
    return commentMapper.toCommentDto(comment);
  }

  @Override
  public List<ItemFullDto> getUserItems(Long userId, Pageable pageable) {
    List<Item> items = itemRepository.getAllByOwnerId(userId);
    return items.stream()
            .map(item -> getById(userId, item.getId()))
            .sorted(Comparator.comparing(ItemFullDto::getId))
            .collect(Collectors.toList());
  }

  private Booking getNextBooking(List<Booking> bookingList) {
    return bookingList.stream()
            .filter(booking -> booking.getStatus().equals(BookingStatus.APPROVED.name()))
            .sorted(Comparator.comparing(Booking::getEnd))
            .filter(booking -> booking.getEnd().isAfter(LocalDateTime.now()))
            .findFirst()
            .orElse(null);
  }

  private Booking getLastBooking(List<Booking> bookingList) {
    return bookingList.stream()
            .filter(booking -> booking.getStatus().equals(BookingStatus.APPROVED.name()))
            .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
            .max(Comparator.comparing(Booking::getStart))
            .orElse(null);
  }

  @Override
  public ItemFullDto getById(Long userId, Long id) {
    Item item = itemRepository.findById(id)
            .orElseThrow(NotFoundException::new);

    List<Booking> bookingList = bookingRepository.findBookingsByItemId(item.getId());
    Booking lastBooking = null;
    Booking nextBooking = null;
    boolean flag = bookingList.stream()
            .map(Booking::getBooker)
            .map(User::getId)
            .noneMatch(it -> it.equals(userId));
    if (bookingList.size() == 1) {
      lastBooking = getNextBooking(bookingList);
    } else if (flag) {
      lastBooking = getLastBooking(bookingList);
      nextBooking = getNextBooking(bookingList);
    }
    List<Comment> comments = commentRepository.findAllByItemId(item.getId());
    List<CommentDto> commentsDTO = comments.stream()
            .map(commentMapper::toCommentDto)
            .collect(Collectors.toList());
    return itemMapper.toItemFullDto(item, commentsDTO, lastBooking, nextBooking);
  }
}
