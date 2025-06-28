package ru.skypro.homework.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class CreateOrUpdateComment {

    @Size(min = 8, max = 64)
    private String text;
}