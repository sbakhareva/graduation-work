package ru.skypro.homework.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import ru.skypro.homework.dto.Login;
import ru.skypro.homework.dto.Role;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id;

    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String password;
    private Role role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserImage image;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user")
    private List<AdEntity> ads;
}
