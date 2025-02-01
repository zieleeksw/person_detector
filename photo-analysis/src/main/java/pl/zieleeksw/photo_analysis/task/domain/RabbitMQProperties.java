package pl.zieleeksw.photo_analysis.task.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
class RabbitMQProperties {

    @Value("${rabbitmq.queue.name}")
    private String queueName;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;


    String queueName() {
        return queueName;
    }

    String exchangeName() {
        return exchangeName;
    }

    String routingKey() {
        return routingKey;
    }
}
