package ru.skypro.homework.model;

import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Comment {

    private Integer authorId; // id автора комментария
    private String authorImage;
    private String authorName;
    private Integer createdAt;
    private Integer commentId; // id комментария
    private String text;
}
