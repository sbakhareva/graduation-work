package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.model.CommentEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    Optional<CommentEntity> findById(Integer id);

    List<CommentEntity> findAllByAdId(Integer id);

    void deleteById(Integer id);

    boolean existsById(Integer id);
}
