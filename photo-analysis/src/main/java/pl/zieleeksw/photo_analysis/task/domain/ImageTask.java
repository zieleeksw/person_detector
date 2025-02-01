package pl.zieleeksw.photo_analysis.task.domain;

import java.util.UUID;

record ImageTask(
        UUID id,
        byte[] image
) {

}
