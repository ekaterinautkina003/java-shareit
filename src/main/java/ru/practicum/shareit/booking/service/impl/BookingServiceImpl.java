package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.RequestBookingStatus;
import ru.practicum.shareit.booking.repostiory.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnknownStateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.ValidationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final EntityManager entityManager;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    public BookingDto create(Long userId, BookingRequestDto bookingDto) {
        validateDate(bookingDto);
        User user = userRepository.findById(userId)
                .orElseThrow(NotFoundException::new);
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(NotFoundException::new);
        if (userId.equals(item.getOwner().getId())) {
            throw new NotFoundException();
        }
        if (!itemRepository.isItemAvalible(bookingDto.getItemId())) {
            throw new ValidationException();
        }

        bookingDto.setStatus(BookingStatus.WAITING.name());
        Booking result = bookingRepository.save(bookingMapper.toBooking(bookingDto, user, item)
        );
        return bookingMapper.toBookingDto(result);
    }

    @Override
    @Transactional
    public BookingDto update(Long userId, Long bookingId, boolean approved) {
        userRepository.findById(userId).orElseThrow(NotFoundException::new);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(NotFoundException::new);
        if (BookingStatus.valueOf(booking.getStatus()).equals(BookingStatus.APPROVED)) {
            throw new ValidationException();
        }
        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(NotFoundException::new);

        if (!userId.equals(item.getOwner().getId())) {
            throw new NotFoundException();
        }
        if (approved) {
            itemRepository.updateItemAvailableById(item.getId(), approved);
            bookingRepository.updateBookingStatusById(bookingId, BookingStatus.APPROVED.name());
        } else {
            bookingRepository.updateBookingStatusById(bookingId, BookingStatus.REJECTED.name());
        }
        Booking book = bookingRepository.findById(bookingId)
                .orElseThrow(NotFoundException::new);
        entityManager.refresh(book);
        return bookingMapper.toBookingDto(book);
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        User user = userRepository.findById(userId)
                .orElseThrow(NotFoundException::new);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(NotFoundException::new);
        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(NotFoundException::new);
        if (!userId.equals(item.getOwner().getId())
                && !userId.equals(booking.getBooker().getId())) {
            throw new NotFoundException();
        }
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsByUser(
            Long userId,
            String state,
            Pageable pageable
    ) {
        userRepository.findById(userId)
                .orElseThrow(NotFoundException::new);
        List<Booking> result;
        try {
            RequestBookingStatus status = RequestBookingStatus.valueOf(state);
            result = findBookingsByUserIdAndStatus(userId, status, pageable);
        } catch (IllegalArgumentException exception) {
            throw new UnknownStateException(state);
        }
        return result.stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingStatusByOwner(
            Long userId,
            String state,
            Pageable pageable
    ) {
        userRepository.findById(userId)
                .orElseThrow(NotFoundException::new);
        List<Booking> result;
        try {
            RequestBookingStatus status = RequestBookingStatus.valueOf(state);
            result = findBookingsByOwnerIdAndStatus(userId, status, pageable);
        } catch (IllegalArgumentException exception) {
            throw new UnknownStateException(state);
        }
        return result.stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private List<Booking> findBookingsByOwnerIdAndStatus(
            Long ownerId,
            RequestBookingStatus state,
            Pageable pageable
    ) {
        switch (state) {
            case ALL:
                return bookingRepository.findBookingsByItem_OwnerIdOrderByStartDesc(ownerId, pageable);
            case WAITING:
                return bookingRepository.findBookingsByItem_OwnerIdAndStatusOrderByStartDesc(ownerId,
                        BookingStatus.WAITING.name(), pageable);
            case REJECTED:
                return bookingRepository.findBookingsByItem_OwnerIdAndStatusOrderByStartDesc(ownerId,
                        BookingStatus.REJECTED.name(), pageable);
            case CURRENT:
                return bookingRepository.findCurrentBookingByOwnerId(ownerId, pageable);
            case PAST:
                return bookingRepository.findBookingsByItem_OwnerIdAndEndBeforeOrderByStartDesc(ownerId,
                        LocalDateTime.now(), pageable);
            case FUTURE:
                return bookingRepository.findBookingsByItem_OwnerIdAndStartAfterOrderByStartDesc(ownerId,
                        LocalDateTime.now(), pageable);
            default:
                throw new UnknownStateException(state.name());
        }
    }

    private List<Booking> findBookingsByUserIdAndStatus(
            Long bookerId,
            RequestBookingStatus state,
            Pageable pageable
    ) {
        switch (state) {
            case ALL:
                return bookingRepository.findBookingByBookerIdOrderByStartDesc(bookerId, pageable);
            case WAITING:
                return bookingRepository.findBookingByBookerIdAndStatusOrderByStartDesc(bookerId,
                        BookingStatus.WAITING.name(), pageable);
            case REJECTED:
                return bookingRepository.findBookingByBookerIdAndStatusOrderByStartDesc(bookerId,
                        BookingStatus.REJECTED.name(), pageable);
            case CURRENT:
                return bookingRepository.findCurrentBookingByBookerId(bookerId, pageable);
            case PAST:
                return bookingRepository.findPastBookingByBookerId(bookerId, pageable);
            case FUTURE:
                return bookingRepository.findFutureBookingByBookerId(bookerId, pageable);
            default:
                throw new UnknownStateException(state.name());
        }
    }

    private void validateDate(BookingRequestDto bookingParam) {
        if (bookingParam.getStart().equals(bookingParam.getEnd())
                || bookingParam.getStart().isAfter(bookingParam.getEnd())
        ) {
            throw new ValidationException();
        }
    }
}
