package pl.zieleeksw.photo_analysis.task.dto;

import java.util.UUID;

public record ImageTaskDto(
        UUID id,
        byte[] image
) {

}
