package ru.skypro.homework.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Entity
@Table(name = "comments")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id; // id комментария

    private Integer authorId; // id автора комментария
    private String authorImage;
    private String authorFirstName;
    private long createdAt;
    private String text;

    @ManyToOne
    @JoinColumn(name = "ad_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AdEntity ad;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now().toEpochMilli();
    }
}
