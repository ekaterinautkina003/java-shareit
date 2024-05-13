package ru.practicum.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.model.Comment;

@Component
public class CommentMapper {

  public CommentDto toCommentDto(Comment comment) {
    return CommentDto.builder()
            .id(comment.getId())
            .text(comment.getText())
            .authorName(comment.getUser().getName())
            .created(comment.getCreated())
            .build();
  }
}
