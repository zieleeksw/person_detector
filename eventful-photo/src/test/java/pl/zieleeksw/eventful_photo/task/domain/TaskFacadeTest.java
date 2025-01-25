package pl.zieleeksw.eventful_photo.task.domain;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import pl.zieleeksw.eventful_photo.task.dto.CreatedTaskDto;
import pl.zieleeksw.eventful_photo.task.dto.TaskDto;
import pl.zieleeksw.eventful_photo.task.dto.TaskException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TaskFacadeTest {

    private final TaskFacade facade = new TaskConfiguration().taskFacade();

    @Test
    void shouldSaveTask() {
        byte[] imageBytes = "image-data".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", imageBytes);

        CreatedTaskDto result = facade.save(file);

        assertNotNull(result);
        assertNotNull(result.id());

        TaskDto savedTask = facade.findById(result.id());
        assertNotNull(savedTask);
        assertEquals(Status.PENDING, Status.valueOf(savedTask.status().toString()));
        assertEquals(0, savedTask.detectedPersons());
    }

    @Test
    void shouldThrowTaskExceptionWhenFileCannotBeRead() {
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", (byte[]) null);

        TaskException exception = assertThrows(TaskException.class, () -> facade.save(file));
        assertEquals("File is empty", exception.getMessage());
    }

    @Test
    void shouldFindTaskByIdAndReturnTaskDto() {
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", "image-data".getBytes());
        CreatedTaskDto created = facade.save(file);

        TaskDto result = facade.findById(created.id());

        assertNotNull(result);
        assertEquals(created.id(), result.id());
        assertEquals(Status.PENDING, Status.valueOf(result.status().toString()));
        assertEquals(0, result.detectedPersons());
    }

    @Test
    void shouldThrowTaskExceptionWhenTaskNotFoundById() {
        UUID nonExistentId = UUID.randomUUID();

        TaskException exception = assertThrows(TaskException.class, () -> facade.findById(nonExistentId));
        assertEquals((String.format("Task with id %s not found", nonExistentId)), exception.getMessage());
    }
}