package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends CrudRepository<Item, Long> {

  Optional<Item> findById(Long itemId);

  List<Item> getItemsByOwnerId(Long userId, Pageable pageable);

  List<Item> getAllByOwnerId(Long userId);

  @Query("select i.available from ItemEntity i where i.id = ?1")
  boolean isItemAvalible(Long id);

  @Modifying(clearAutomatically = true)
  @Query("update ItemEntity i set i.available = :available where i.id = :id")
  void updateItemAvailableById(@Param("id") Long id, @Param("available") boolean available);

  @Query("select i from ItemEntity i where i.available = true "
          + "and upper(i.description) like upper(concat('%', ?1, '%')) "
          + "or upper(i.name) like upper(concat('%', ?1, '%')) "
          + "group by i.id")
  List<Item> searchByText(String text, Pageable pageable);

  List<Item> getAllByRequestId(Long requestId);
}
