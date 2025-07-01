package ru.skypro.homework.dto;

import lombok.*;

import java.util.List;

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