package pl.zieleeksw.eventful_photo.task;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.zieleeksw.eventful_photo.task.domain.TaskFacade;
import pl.zieleeksw.eventful_photo.task.dto.CreatedTaskDto;
import pl.zieleeksw.eventful_photo.task.dto.StatusDto;
import pl.zieleeksw.eventful_photo.task.dto.TaskDto;
import pl.zieleeksw.eventful_photo.task.dto.TaskStatusDetectedPersonsDto;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
class TaskController {

    private final TaskFacade taskFacade;

    TaskController(TaskFacade taskFacade) {
        this.taskFacade = taskFacade;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CreatedTaskDto saveTaskFromFile(
            @RequestParam("file") MultipartFile file
    ) {
        return taskFacade.save(file);
    }

    @PostMapping("/url")
    @ResponseStatus(HttpStatus.CREATED)
    CreatedTaskDto saveTaskFromUrl(
            @RequestParam("url") String url
    ) {
        return taskFacade.save(url);
    }

    @GetMapping
    Set<TaskDto> getTasks(
    ) {
        return taskFacade.findAll();
    }

    @GetMapping("/{id}")
    TaskDto getTaskById(
            @PathVariable UUID id
    ) {
        return taskFacade.findById(id);
    }

    @PutMapping("/{id}/status")
    void updateTaskStatus(
            @PathVariable UUID id, @RequestBody StatusDto statusDto
    ) {
        taskFacade.updateStatus(id, statusDto);
    }

    @PutMapping("/{id}/status/detected-persons")
    void updateTaskStatusAndDetectedPersons(
            @PathVariable UUID id,
            @RequestBody TaskStatusDetectedPersonsDto dto
    ) {
        taskFacade.updateStatusAndDetectedPersons(id, dto);
    }

    @GetMapping("/count")
    Map<StatusDto, Long> getTaskCountByStatus(
    ) {
        return taskFacade.getTaskCountByStatus();
    }
}
