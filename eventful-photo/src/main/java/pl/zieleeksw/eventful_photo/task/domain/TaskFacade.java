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
    private final HttpImageClient httpImageClient;
    private final TaskProducer taskProducer;

    public TaskFacade(TaskRepository repository,
                      HttpImageClient httpImageClient,
                      TaskProducer taskProducer) {
        this.repository = repository;
        this.httpImageClient = httpImageClient;
        this.taskProducer = taskProducer;
    }

    public CreatedTaskDto save(String url) {
        try {
            byte[] imageBytes = httpImageClient.download(url);

            Task task = new Task(imageBytes);
            Task savedTask = repository.save(task);
            UUID savedTaskId = savedTask.getId();
            taskProducer.sendTask(savedTaskId);
            return new CreatedTaskDto(savedTaskId);
        } catch (IOException e) {
            throw new TaskException(String.format("Failed to download image from URL: %s", e.getMessage()));
        }
    }

    public CreatedTaskDto save(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new TaskException("File is empty");
            }

            Task task = new Task(file.getBytes());
            Task savedTask = repository.save(task);
            UUID savedTaskId = savedTask.getId();
            taskProducer.sendTask(savedTaskId);
            return new CreatedTaskDto(savedTaskId);
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
