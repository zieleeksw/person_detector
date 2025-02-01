package pl.zieleeksw.photo_analysis.task;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pl.zieleeksw.photo_analysis.task.domain.TaskFacade;

@Component
class TaskConsumer {

    private final TaskFacade taskFacade;

    TaskConsumer(TaskFacade taskFacade) {
        this.taskFacade = taskFacade;
    }

    @RabbitListener(queues = "#{@rabbitMQQueueName}")
    void consumeTask(String taskId) {
        taskFacade.consumeTask(taskId);
    }
}