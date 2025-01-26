package pl.zieleeksw.eventful_photo.task.domain;


import org.springframework.data.repository.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

interface TaskRepository extends Repository<Task, UUID> {

    Task save(Task task);

    Optional<Task> findById(UUID id);

    long countByStatus(Status status);

    Set<Task> findAll();
}
