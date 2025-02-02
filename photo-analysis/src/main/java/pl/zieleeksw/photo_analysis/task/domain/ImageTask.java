package pl.zieleeksw.photo_analysis.task.domain;

import pl.zieleeksw.photo_analysis.task.dto.ImageTaskDto;

import java.util.UUID;

record ImageTask(
        UUID id,
        byte[] image
) {
    static ImageTask from(ImageTaskDto imageTaskDto) {
        return new ImageTask(
                imageTaskDto.id(),
                imageTaskDto.image()
        );
    }
}
