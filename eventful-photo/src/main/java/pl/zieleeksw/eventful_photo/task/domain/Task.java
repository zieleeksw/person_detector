package pl.zieleeksw.eventful_photo.task.domain;

import jakarta.persistence.*;
import pl.zieleeksw.eventful_photo.task.dto.StatusDto;
import pl.zieleeksw.eventful_photo.task.dto.TaskDto;
import pl.zieleeksw.eventful_photo.task.dto.TaskImageDto;

import java.util.UUID;

@Entity
class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Integer detectedPersons;

    @Lob
    private byte[] image;

    protected Task() {
    }

    Task(byte[] image) {
        this.image = image;
        this.status = Status.PENDING;
        this.detectedPersons = 0;
    }

    public Task(Status status, Integer detectedPersons, byte[] image) {
        this.status = status;
        this.detectedPersons = detectedPersons;
        this.image = image;
    }

    UUID getId() {
        return id;
    }

    void setId(UUID id) {
        this.id = id;
    }

    Status getStatus() {
        return status;
    }

    void setStatus(Status status) {
        this.status = status;
    }

    Integer getDetectedPersons() {
        return detectedPersons;
    }

    void setDetectedPersons(Integer detectedPersons) {
        this.detectedPersons = detectedPersons;
    }

    byte[] getImage() {
        return image;
    }

    void setImage(byte[] image) {
        this.image = image;
    }

    TaskDto dto() {
        return new TaskDto(
                id,
                StatusDto.valueOf(status.toString()),
                detectedPersons
        );
    }

    TaskImageDto imageDto() {
        return new TaskImageDto(
                id,
                image
        );
    }
}
