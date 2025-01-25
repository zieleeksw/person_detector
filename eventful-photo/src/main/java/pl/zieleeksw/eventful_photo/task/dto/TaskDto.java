package pl.zieleeksw.eventful_photo.task.dto;

import java.util.UUID;

public record TaskDto(
        UUID id,
        StatusDto status,
        Integer detectedPersons
) {
}
