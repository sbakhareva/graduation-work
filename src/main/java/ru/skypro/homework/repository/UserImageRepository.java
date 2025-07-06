package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.model.UserImage;

import java.util.Optional;

@Repository
public interface UserImageRepository extends JpaRepository<UserImage, Long> {

    Optional<UserImage> findByUserId(Integer id);

    void deleteByUserId(Integer id);

    boolean existsByUserId(Integer id);
}
