package ru.skypro.homework.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ads_images")
@Data
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
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @JsonIgnore
    private byte[] preview;

    @OneToOne
    @JoinColumn(name = "ad_id")
    @JsonIgnore
    private AdEntity ad;
}
