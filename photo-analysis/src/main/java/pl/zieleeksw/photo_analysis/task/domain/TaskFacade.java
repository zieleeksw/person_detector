package pl.zieleeksw.photo_analysis.task.domain;

import org.springframework.web.reactive.function.client.WebClient;
import pl.zieleeksw.photo_analysis.task.dto.StatusDto;
import pl.zieleeksw.photo_analysis.task.dto.TaskStatusDetectedPersonsDto;

public class TaskFacade {

    private final WebClient.Builder webClientBuilder;
    private final ImageProcessor processor;

    TaskFacade(WebClient.Builder webClientBuilder, ImageProcessor processor) {
        this.webClientBuilder = webClientBuilder;
        this.processor = processor;
    }

    public void consumeTask(String taskId) {
        String taskUrl = "http://eventful-photo:8080/api/v1/tasks/" + taskId + "/image";

        ImageTask task = webClientBuilder.baseUrl(taskUrl)
                .build()
                .get()
                .retrieve()
                .bodyToMono(ImageTask.class)
                .block();

        if (task != null) {
            updateTaskStatus(task.id().toString(), StatusDto.IN_PROGRESS.toString());
        }

        try {
            byte[] bytes = task.image();
            int detectedPersons = processor.process(bytes);
            updateTaskStatusAndDetectedPersons(taskId, StatusDto.COMPLETED, detectedPersons);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void updateTaskStatusAndDetectedPersons(String taskId, StatusDto status, int detectedPersons) {
        String url = "http://eventful-photo:8080/api/v1/tasks/" + taskId + "/status/detected-persons";

        TaskStatusDetectedPersonsDto dto = new TaskStatusDetectedPersonsDto(
                StatusDto.valueOf(status.name()),
                detectedPersons
        );

        webClientBuilder.baseUrl(url)
                .build()
                .put()
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    private void updateTaskStatus(String taskId, String status) {
        String url = "http://eventful-photo:8080/api/v1/tasks/" + taskId + "/status";

        webClientBuilder.baseUrl(url)
                .build()
                .put()
                .bodyValue(status)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
