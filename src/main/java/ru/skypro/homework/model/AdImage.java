package ru.skypro.homework.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "ads_images")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id;

    private String filePath;
    private long fileSize;
    private String mediaType;

    @OneToOne
    @JoinColumn(name = "ad_id")
    @JsonIgnore
    private AdEntity ad;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AdImage adImage = (AdImage) o;
        return fileSize == adImage.fileSize
                && Objects.equals(id, adImage.id)
                && Objects.equals(filePath, adImage.filePath)
                && Objects.equals(mediaType, adImage.mediaType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, filePath, fileSize, mediaType);
    }

    @Override
    public String toString() {
        return "AdImage{" +
                "id=" + id +
                ", filePath='" + filePath + '\'' +
                ", fileSize=" + fileSize +
                ", mediaType='" + mediaType + '\'' +
                '}';
    }
}
