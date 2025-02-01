package pl.zieleeksw.photo_analysis.task.domain;

import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
class YoloConfig {

    @Value("classpath:${yolo.weights}")
    private Resource modelWeightsResource;

    @Value("classpath:${yolo.config}")
    private Resource modelConfigResource;

    @Value("classpath:${yolo.classes}")
    private Resource classesFileResource;

    private final Path tempDir;

    YoloConfig() throws IOException {
        this.tempDir = Files.createTempDirectory("yolo");
    }

    String weights() throws IOException {
        return copyToTempFile(modelWeightsResource, "yolov4.weights");
    }

    String config() throws IOException {
        return copyToTempFile(modelConfigResource, "yolov4.cfg");
    }

    List<String> classes() throws IOException {
        List<String> classes = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(classesFileResource.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                classes.add(line);
            }
        }
        return classes;
    }

    Net yolo() throws IOException {
        Net net = Dnn.readNetFromDarknet(config(), weights());
        net.setPreferableBackend(Dnn.DNN_BACKEND_OPENCV);
        net.setPreferableTarget(Dnn.DNN_TARGET_CPU);
        return net;
    }

    private String copyToTempFile(Resource resource, String fileName) throws IOException {
        Path tempFile = tempDir.resolve(fileName);
        try (InputStream is = resource.getInputStream(); OutputStream os = Files.newOutputStream(tempFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
        return tempFile.toAbsolutePath().toString();
    }
}
