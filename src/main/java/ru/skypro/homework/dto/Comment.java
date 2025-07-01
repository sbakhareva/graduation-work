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
}
