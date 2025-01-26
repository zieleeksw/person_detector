package pl.zieleeksw.eventful_photo.task.dto;

import java.util.UUID;

public record TaskImageDto(
        UUID id,
        byte[] image
) {
}
