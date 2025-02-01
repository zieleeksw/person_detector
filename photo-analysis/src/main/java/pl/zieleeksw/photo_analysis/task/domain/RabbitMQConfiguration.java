package pl.zieleeksw.photo_analysis.task.domain;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class RabbitMQConfiguration {

    private final RabbitMQProperties properties;

    RabbitMQConfiguration(RabbitMQProperties properties) {
        this.properties = properties;
    }

    @Bean
    Queue taskQueue() {
        return new Queue(properties.queueName(), true);
    }

    @Bean
    DirectExchange taskExchange() {
        return new DirectExchange(properties.exchangeName());
    }

    @Bean
    Binding taskBinding(Queue taskQueue, DirectExchange taskExchange) {
        return BindingBuilder.bind(taskQueue).to(taskExchange).with(properties.routingKey());
    }

    @Bean
    String rabbitMQQueueName() {
        return properties.queueName();
    }
}
