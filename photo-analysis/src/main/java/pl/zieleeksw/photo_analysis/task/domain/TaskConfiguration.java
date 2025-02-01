package pl.zieleeksw.photo_analysis.task.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
class TaskConfiguration {

    @Bean
    TaskFacade taskFacade(WebClient.Builder webClientBuilder, YoloConfig yoloConfig) {
        return new TaskFacade(
                webClientBuilder,
                new ImageProcessor(yoloConfig)
        );
    }
}
