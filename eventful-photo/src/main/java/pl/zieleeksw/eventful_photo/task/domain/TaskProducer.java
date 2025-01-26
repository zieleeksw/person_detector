package pl.zieleeksw.eventful_photo.task.domain;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.UUID;

class TaskProducer {

    private final RabbitTemplate rabbitTemplate;

    TaskProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    void sendTask(UUID taskId) {
        rabbitTemplate.convertAndSend(RabbitMQConfiguration.EXCHANGE_NAME, "taskRoutingKey", taskId.toString());
    }
}
