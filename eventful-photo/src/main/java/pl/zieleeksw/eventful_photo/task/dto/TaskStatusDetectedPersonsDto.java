package pl.zieleeksw.eventful_photo.task.dto;

public record TaskStatusDetectedPersonsDto(
        StatusDto status,
        Integer detectedPersons
) {
}
