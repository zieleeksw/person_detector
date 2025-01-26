package pl.zieleeksw.eventful_photo.task.domain;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.mock.web.MockMultipartFile;
import pl.zieleeksw.eventful_photo.task.dto.*;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class TaskFacadeTest {

    private final RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
    private final HttpImageClient httpImageClient = mock(HttpImageClient.class);
    private TaskFacade facade = new TaskConfiguration().taskFacade(httpImageClient, rabbitTemplate);

    @Test
    void shouldSaveTaskFromMultiPartFile() {
        byte[] imageBytes = "image-data".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", imageBytes);
        CreatedTaskDto result = facade.save(file);

        assertNotNull(result);
        assertNotNull(result.id());

        TaskDto savedTask = facade.findById(result.id());

        assertNotNull(savedTask);
        assertEquals(Status.PENDING, Status.valueOf(savedTask.status().toString()));
        assertEquals(0, savedTask.detectedPersons());
        verify(rabbitTemplate).convertAndSend(
                eq("taskExchange"),
                eq("taskRoutingKey"),
                eq(result.id().toString())
        );
    }

    @Test
    void shouldThrowTaskExceptionWhenFileCannotBeRead() {
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", (byte[]) null);

        TaskException exception = assertThrows(TaskException.class, () -> facade.save(file));
        assertEquals("File is empty", exception.getMessage());
    }

    @Test
    void shouldSaveTaskFromUrlAndSendMessage() throws IOException {
        String validUrl = "http://valid-url.com/image.jpg";
        byte[] imageBytes = "image-data".getBytes();

        HttpImageClient imageClient = mock(HttpImageClient.class);
        when(imageClient.download(validUrl)).thenReturn(imageBytes);

        facade = new TaskFacade(new InMemoryTaskRepository(), imageClient, new TaskProducer(rabbitTemplate));
        CreatedTaskDto result = facade.save(validUrl);

        assertNotNull(result);
        assertNotNull(result.id());

        TaskDto savedTask = facade.findById(result.id());

        assertNotNull(savedTask);
        assertEquals(Status.PENDING, Status.valueOf(savedTask.status().toString()));
        assertEquals(0, savedTask.detectedPersons());
        verify(rabbitTemplate).convertAndSend(
                eq("taskExchange"),
                eq("taskRoutingKey"),
                eq(result.id().toString())
        );
    }

    @Test
    void shouldThrowExceptionWhenUrlIsInvalid() {
        String invalidUrl = "http://invalid-url.com/image.jpg";

        TaskException exception = assertThrows(TaskException.class, () -> facade.save(invalidUrl));
        assertTrue(exception.getMessage().contains("Failed to download image"));
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


    @Test
    void shouldReturnTaskCountByStatus() {
        byte[] imageBytes = "image-data".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", imageBytes);

        // extra one
        facade.save(file);

        CreatedTaskDto saved2 = facade.save(file);
        CreatedTaskDto saved3 = facade.save(file);

        facade.updateStatus(saved2.id(), StatusDto.COMPLETED);
        facade.updateStatus(saved3.id(), StatusDto.FAILED);


        Map<StatusDto, Long> result = facade.getTaskCountByStatus();

        assertEquals(1L, result.get(StatusDto.PENDING));
        assertEquals(1L, result.get(StatusDto.COMPLETED));
        assertEquals(1L, result.get(StatusDto.FAILED));
        assertEquals(0L, result.get(StatusDto.IN_PROGRESS));
    }

    @Test
    void shouldReturnZeroForAllStatusesWhenRepositoryIsEmpty() {
        Map<StatusDto, Long> result = facade.getTaskCountByStatus();

        assertAll(
                () -> assertEquals(0L, result.get(StatusDto.PENDING)),
                () -> assertEquals(0L, result.get(StatusDto.COMPLETED)),
                () -> assertEquals(0L, result.get(StatusDto.FAILED)),
                () -> assertEquals(0L, result.get(StatusDto.IN_PROGRESS))
        );
    }

    @Test
    void shouldUpdateStatusWhenTaskExists() {
        byte[] imageBytes = "image-data".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", imageBytes);
        CreatedTaskDto saved = facade.save(file);

        TaskDto byId = facade.findById(saved.id());
        assertEquals(Status.PENDING, Status.valueOf(byId.status().toString()));

        facade.updateStatus(byId.id(), StatusDto.IN_PROGRESS);

        TaskDto updated = facade.findById(byId.id());
        assertEquals(Status.IN_PROGRESS, Status.valueOf(updated.status().toString()));
    }

    @Test
    void shouldThrowTaskExceptionWhenTaskNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        StatusDto newStatus = StatusDto.COMPLETED;

        TaskException exception = assertThrows(TaskException.class, () -> facade.updateStatus(nonExistentId, newStatus));
        assertEquals(String.format("Task with id %s not found", nonExistentId), exception.getMessage());
    }

    @Test
    void shouldUpdateStatusAndDetectedPersonsWhenTaskExists() {
        byte[] imageBytes = "image-data".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", imageBytes);

        CreatedTaskDto saved = facade.save(file);

        TaskDto byId = facade.findById(saved.id());
        assertEquals(Status.PENDING, Status.valueOf(byId.status().toString()));
        assertEquals(0, byId.detectedPersons());

        TaskStatusDetectedPersonsDto dto = new TaskStatusDetectedPersonsDto(StatusDto.COMPLETED, 5);

        facade.updateStatusAndDetectedPersons(saved.id(), dto);

        TaskDto updated = facade.findById(saved.id());
        assertEquals(Status.COMPLETED, Status.valueOf(updated.status().toString()));
        assertEquals(5, updated.detectedPersons());
    }

    @Test
    void shouldThrowTaskExceptionWhenTaskNotFoundForUpdateStatusAndDetectedPersons() {
        UUID nonExistentId = UUID.randomUUID();
        TaskStatusDetectedPersonsDto dto = new TaskStatusDetectedPersonsDto(StatusDto.COMPLETED, 5);

        TaskException exception = assertThrows(TaskException.class, () -> facade.updateStatusAndDetectedPersons(nonExistentId, dto));
        assertEquals(String.format("Task with id %s not found", nonExistentId), exception.getMessage());
    }
}