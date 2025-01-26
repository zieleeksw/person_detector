package pl.zieleeksw.eventful_photo.task.domain;

import java.util.*;

class InMemoryTaskRepository implements TaskRepository {

    private final Map<UUID, Task> database = new HashMap<>();

    @Override
    public Task save(Task entity) {
        UUID id = entity.getId() != null ? entity.getId() : UUID.randomUUID();
        Task task = new Task(
                entity.getStatus(),
                entity.getDetectedPersons(),
                entity.getImage()
        );
        task.setId(id);
        database.put(id, task);
        return task;
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return Optional.ofNullable(database.get(id));
    }

    @Override
    public long countByStatus(Status status) {
        return database.values().stream()
                .filter(task -> task.getStatus() == status)
                .count();
    }

    @Override
    public Set<Task> findAll() {
        return new HashSet<>(database.values());
    }
}
