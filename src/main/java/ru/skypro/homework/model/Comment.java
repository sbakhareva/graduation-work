package ru.skypro.homework.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer commentId; // id комментария

    private Integer authorId; // id автора комментария
    private String authorImage;
    private String authorName;
    private Integer createdAt;
    private String text;

    @ManyToOne
    @JoinColumn(name = "ad_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Ad ad;
}
