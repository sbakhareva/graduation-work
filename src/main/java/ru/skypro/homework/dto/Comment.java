package ru.skypro.homework.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Comment {

    private Integer author; // id автора комментария
    private String authorImage;
    private String authorName;
    private Integer createdAt;
    private Integer pk; // id комментария
    private String text;

    // Метод для преобразования из сущности в DTO
    public static Comment fromEntity(ru.skypro.homework.model.Comment commentEntity) {
        if (commentEntity == null) {
            return null;
        }
        Comment comment = new Comment();
        comment.setAuthor(commentEntity.getAuthorId());
        comment.setAuthorImage(commentEntity.getAuthorImage());
        comment.setAuthorName(commentEntity.getAuthorName());
        comment.setCreatedAt(commentEntity.getCreatedAt());
        comment.setPk(commentEntity.getCommentId());
        comment.setText(commentEntity.getText());
        return comment;
    }

    // Метод для преобразования из DTO в сущность
    public static ru.skypro.homework.model.Comment toEntity(Comment commentDto) {
        if (commentDto == null) {
            return null;
        }
        ru.skypro.homework.model.Comment commentEntity = new ru.skypro.homework.model.Comment();
        commentEntity.setCommentId(commentDto.getPk());
        commentEntity.setText(commentDto.getText());
        commentEntity.setCreatedAt(commentDto.getCreatedAt());
        commentEntity.setAuthorId(commentDto.getAuthor()); // Установка id автора
        // Установка объявления, если необходимо
        // commentEntity.setAd(new Ad(adId));
        return commentEntity;
    }
}
