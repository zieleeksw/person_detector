package pl.zieleeksw.eventful_photo.task.domain;

import org.springframework.web.multipart.MultipartFile;
import pl.zieleeksw.eventful_photo.task.dto.CreatedTaskDto;
import pl.zieleeksw.eventful_photo.task.dto.TaskDto;
import pl.zieleeksw.eventful_photo.task.dto.TaskException;

import java.io.IOException;
import java.util.UUID;

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
}
