package ru.skypro.homework.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

/**
 * Сущность фото пользователя, представляющая запись в таблице `user_images` базы данных.
 * Используется для хранения данных о фото пользователя.
 */

@Entity
@Table(name = "user_images")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id;

    private String filePath;
    private long fileSize;
    private String mediaType;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private UserEntity user;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserImage userImage = (UserImage) o;
        return fileSize == userImage.fileSize
                && Objects.equals(id, userImage.id)
                && Objects.equals(filePath, userImage.filePath)
                && Objects.equals(mediaType, userImage.mediaType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, filePath, fileSize, mediaType);
    }

    @Override
    public String toString() {
        return "UserImage{" +
                "id=" + id +
                ", filePath='" + filePath + '\'' +
                ", fileSize=" + fileSize +
                ", mediaType='" + mediaType + '\'' +
                '}';
    }
}
