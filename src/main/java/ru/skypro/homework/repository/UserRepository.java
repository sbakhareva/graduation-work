package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.model.UserEntity;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @EntityGraph(attributePaths = {"image"})
    Optional<UserEntity> findById(Integer id);

    @EntityGraph(attributePaths = {"image"})
    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
