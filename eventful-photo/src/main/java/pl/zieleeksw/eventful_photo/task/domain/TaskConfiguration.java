package pl.zieleeksw.eventful_photo.task.domain;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class TaskConfiguration {

    @Bean
    TaskFacade taskFacade(TaskRepository taskRepository, RabbitTemplate template) {
        return new TaskFacade(
                taskRepository,
                new HttpImageClient(),
                new TaskProducer(template)
        );
    }

    TaskFacade taskFacade(HttpImageClient client, RabbitTemplate template) {
        return taskFacade(
                new InMemoryTaskRepository(),
                template
        );
    }
}
