package pl.zieleeksw.photo_analysis.task.dto;

public record TaskStatusDetectedPersonsDto(
        StatusDto  status,
        Integer detectedPersons
) {
}
