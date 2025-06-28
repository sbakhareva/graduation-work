package ru.skypro.homework.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ExtendedAd {
    private Integer pk;
    private String firstName;
    private String lastName;
    private String description;
    private String email;
    private String image;
    private String phone;
    private Integer price;
    private String title;
}