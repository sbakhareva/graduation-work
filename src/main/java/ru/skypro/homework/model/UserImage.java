package ru.skypro.homework.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class UserImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id;

    private String filePath;
    private long fileSize;
    private String mediaType;
    @Lob
    @JsonIgnore
    private byte[] preview;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
}
