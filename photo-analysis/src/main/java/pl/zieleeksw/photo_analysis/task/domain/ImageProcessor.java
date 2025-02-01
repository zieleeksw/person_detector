package pl.zieleeksw.photo_analysis.task.domain;

import org.opencv.core.*;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class ImageProcessor {

    private final YoloConfig yoloConfig;

    ImageProcessor(YoloConfig yoloConfig) {
        this.yoloConfig = yoloConfig;
    }

    Integer process(byte[] bytes) throws IOException {
        Mat matrix = Imgcodecs.imdecode(new MatOfByte(bytes), Imgcodecs.IMREAD_COLOR);
        if (matrix.empty()) {
            throw new IllegalArgumentException("Nie udało się załadować obrazu.");
        }

        Net net = yoloConfig.yolo();
        Mat blob = prepareBlob(matrix);
        net.setInput(blob);

        List<Mat> outputs = performInference(net);
        List<DetectionResult> results = processOutputs(outputs, matrix);

        long detectedPersons = results.stream()
                .filter(result -> result.label().equals("person"))
                .count();

        return (int) detectedPersons;
    }

    private Mat prepareBlob(Mat matrix) {
        Size size = new Size(416, 416);
        return Dnn.blobFromImage(matrix, 1 / 255.0, size, new Scalar(0, 0, 0), true, false);
    }

    private List<Mat> performInference(Net net) {
        List<String> layerNames = net.getLayerNames();
        List<String> outputLayerNames = new ArrayList<>();
        for (Integer i : net.getUnconnectedOutLayers().toList()) {
            outputLayerNames.add(layerNames.get(i - 1));
        }

        List<Mat> outs = new ArrayList<>();
        net.forward(outs, outputLayerNames);
        return outs;
    }

    private List<DetectionResult> processOutputs(List<Mat> outs, Mat matrix) throws IOException {
        List<Integer> classIds = new ArrayList<>();
        List<Float> confidences = new ArrayList<>();
        List<Rect2d> boxes = new ArrayList<>();

        for (Mat out : outs) {
            for (int i = 0; i < out.rows(); i++) {
                Mat row = out.row(i);
                Mat scores = row.colRange(5, row.cols());
                Core.MinMaxLocResult result = Core.minMaxLoc(scores);
                float confidence = (float) result.maxVal;

                if (confidence > 0.5f) {
                    int centerX = (int) (row.get(0, 0)[0] * matrix.cols());
                    int centerY = (int) (row.get(0, 1)[0] * matrix.rows());
                    int width = (int) (row.get(0, 2)[0] * matrix.cols());
                    int height = (int) (row.get(0, 3)[0] * matrix.rows());

                    int x = centerX - width / 2;
                    int y = centerY - height / 2;

                    boxes.add(new Rect2d(x, y, width, height));
                    confidences.add(confidence);
                    classIds.add((int) result.maxLoc.x);
                }
            }
        }

        MatOfFloat matConfidences = new MatOfFloat();
        matConfidences.fromList(confidences);

        MatOfRect2d matBoxes = new MatOfRect2d();
        matBoxes.fromList(boxes);

        MatOfInt indices = new MatOfInt();
        Dnn.NMSBoxes(matBoxes, matConfidences, 0.5f, 0.4f, indices);

        List<DetectionResult> results = new ArrayList<>();
        for (int i : indices.toArray()) {
            Rect2d box = boxes.get(i);
            int classId = classIds.get(i);
            float confidence = confidences.get(i);

            String label = yoloConfig.classes().get(classId);
            results.add(new DetectionResult(label, confidence, box));
        }

        return results;
    }

    private record DetectionResult(String label, float confidence, Rect2d box) {
    }
}
