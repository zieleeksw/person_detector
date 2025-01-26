package pl.zieleeksw.eventful_photo.task.domain;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;

class RabbitMQConfiguration {

    public static final String QUEUE_NAME = "taskQueue";
    public static final String EXCHANGE_NAME = "taskExchange";

    @Bean
    Queue taskQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    DirectExchange taskExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    Binding taskBinding(Queue taskQueue, DirectExchange taskExchange) {
        return BindingBuilder.bind(taskQueue).to(taskExchange).with("taskRoutingKey");
    }
}
