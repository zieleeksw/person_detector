package pl.zieleeksw.eventful_photo.task.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class TaskConfiguration {

    @Bean
    TaskFacade taskFacade(TaskRepository taskRepository) {
        return new TaskFacade(taskRepository);
    }

    TaskFacade taskFacade() {
        return taskFacade(new InMemoryTaskRepository());
    }
}
