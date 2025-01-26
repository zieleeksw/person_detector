package pl.zieleeksw.eventful_photo.task.domain;

import org.springframework.web.multipart.MultipartFile;
import pl.zieleeksw.eventful_photo.task.dto.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class TaskFacade {

    private final TaskRepository repository;

    public TaskFacade(TaskRepository repository) {
        this.repository = repository;
    }

    public CreatedTaskDto save(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new TaskException("File is empty");
            }

            Task task = new Task(file.getBytes());
            Task saved = repository.save(task);
            return new CreatedTaskDto(saved.getId());
        } catch (IOException e) {
            throw new TaskException(e.getMessage());
        }
    }

    public TaskDto findById(UUID id) {
        return repository.findById(id)
                .map(Task::dto)
                .orElseThrow(() -> new TaskException(String.format("Task with id %s not found", id)));
    }

    public Map<StatusDto, Long> getTaskCountByStatus() {
        return Arrays.stream(Status.values())
                .collect(Collectors.toMap(
                        status -> StatusDto.valueOf(status.toString()),
                        repository::countByStatus
                ));
    }

    public void updateStatus(UUID id, StatusDto statusDto) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new TaskException(String.format("Task with id %s not found", id)));
        task.setStatus(Status.valueOf(statusDto.toString()));
        repository.save(task);
    }

    public void updateStatusAndDetectedPersons(UUID id, TaskStatusDetectedPersonsDto dto) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new TaskException(String.format("Task with id %s not found", id)));

        task.setStatus(Status.valueOf(dto.status().toString()));
        task.setDetectedPersons(dto.detectedPersons());
        repository.save(task);
    }
}
