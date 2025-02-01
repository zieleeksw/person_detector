package pl.zieleeksw.photo_analysis.task.domain;

import nu.pattern.OpenCV;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
class ApplicationInitializer implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        OpenCV.loadLocally();
    }
}
