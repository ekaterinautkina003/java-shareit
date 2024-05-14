package ru.practicum.item.repository;

import org.springframework.data.repository.CrudRepository;
import ru.practicum.item.model.Comment;

import java.util.List;

public interface CommentRepository extends CrudRepository<Comment, Long> {

  List<Comment> findAllByItemId(Long itemId);
}
