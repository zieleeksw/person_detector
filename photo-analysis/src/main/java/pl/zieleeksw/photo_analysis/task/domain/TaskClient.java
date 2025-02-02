package pl.zieleeksw.photo_analysis.task.domain;

import pl.zieleeksw.photo_analysis.task.dto.ImageTaskDto;
import pl.zieleeksw.photo_analysis.task.dto.StatusDto;

interface TaskClient {

    ImageTaskDto get(String taskId);

    void update(String taskId, StatusDto status);

    void complete(String taskId, int detectedPersons);
}
