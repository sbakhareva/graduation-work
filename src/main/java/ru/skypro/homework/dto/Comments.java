package ru.skypro.homework.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comments {

    private Integer count;
    private List<Comment> results;
}
