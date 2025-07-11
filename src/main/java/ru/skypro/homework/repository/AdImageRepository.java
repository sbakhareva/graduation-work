package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.model.AdImage;

import java.util.Optional;

@Repository
public interface AdImageRepository extends JpaRepository<AdImage, Long> {
    Optional<AdImage> findByAdId(Integer id);

    void deleteByAdId(Integer id);
}
