package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.model.AdEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdRepository extends JpaRepository<AdEntity, Long> {

    Optional<AdEntity> findById(Integer id);

    boolean existsById(Integer id);

    List<AdEntity> findAllByUserId(Integer id);
}
