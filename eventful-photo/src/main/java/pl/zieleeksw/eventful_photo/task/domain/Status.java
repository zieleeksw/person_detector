package pl.zieleeksw.eventful_photo.task.domain;

import pl.zieleeksw.eventful_photo.task.dto.StatusDto;
import pl.zieleeksw.eventful_photo.task.dto.TaskException;

enum Status {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED;

    public static StatusDto of(Status status) {
        try {
            return StatusDto.valueOf(status.name());
        } catch (IllegalArgumentException e) {
            throw new TaskException(String.format("Invalid status: %s", status));
        }
    }

    public static Status from(StatusDto dto) {
        try {
            return Status.valueOf(dto.name());
        } catch (IllegalArgumentException e) {
            throw new TaskException(String.format("Invalid status: %s", dto));
        }
    }
}
