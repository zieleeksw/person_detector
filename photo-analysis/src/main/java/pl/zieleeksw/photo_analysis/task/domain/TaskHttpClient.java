package pl.zieleeksw.photo_analysis.task.domain;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pl.zieleeksw.photo_analysis.task.dto.ImageTaskDto;
import pl.zieleeksw.photo_analysis.task.dto.StatusDto;
import pl.zieleeksw.photo_analysis.task.dto.TaskStatusDetectedPersonsDto;

@Component
class TaskHttpClient implements TaskClient {

    private final WebClient webClient;

    TaskHttpClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public ImageTaskDto get(String taskId) {
        String taskUrl = "http://eventful-photo:8080/api/v1/tasks/" + taskId + "/image";

        return webClient.get()
                .uri(taskUrl)
                .retrieve()
                .bodyToMono(ImageTaskDto.class)
                .block();
    }

    public void update(String taskId, StatusDto status) {
        String url = "http://eventful-photo:8080/api/v1/tasks/" + taskId + "/status";

        webClient.put()
                .uri(url)
                .bodyValue(status)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void complete(String taskId, int detectedPersons) {
        String url = "http://eventful-photo:8080/api/v1/tasks/" + taskId + "/status/detected-persons";
        TaskStatusDetectedPersonsDto dto = new TaskStatusDetectedPersonsDto(StatusDto.COMPLETED, detectedPersons);

        webClient.put()
                .uri(url)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}