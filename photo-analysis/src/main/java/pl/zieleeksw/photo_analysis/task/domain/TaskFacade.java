package pl.zieleeksw.photo_analysis.task.domain;

import pl.zieleeksw.photo_analysis.task.dto.StatusDto;

public class TaskFacade {

    private final TaskClient client;
    private final ImageProcessor processor;

    public TaskFacade(TaskClient client, ImageProcessor processor) {
        this.client = client;
        this.processor = processor;
    }

    public void detectPersons(String taskId) {
        ImageTask task = ImageTask.from(client.get(taskId));
        client.update(task.id().toString(), StatusDto.IN_PROGRESS);

        try {
            byte[] bytes = task.image();
            int detectedPersons = processor.process(bytes);
            client.complete(taskId, detectedPersons);
        } catch (Exception e) {
            client.update(taskId, StatusDto.FAILED);
            throw new RuntimeException(e);
        }
    }
}
