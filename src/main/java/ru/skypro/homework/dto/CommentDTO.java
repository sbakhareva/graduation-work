package ru.skypro.homework.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {

    private Integer author; // id автора комментария
    private String authorImage;
    private String authorName;
    private Integer createdAt;
    private Integer pk; // id комментария
    private String text;
}
