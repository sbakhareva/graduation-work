package ru.skypro.homework.dto;

import lombok.*;

import java.util.List;

/**
 * ДТО, представляющий собой список комментариев к объявлению.
 * Содержит информацию о количестве комментариев и список ДТО {@link Comment}
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Comments {

    private Integer count;
    private List<Comment> results;
}
