package pl.zieleeksw.eventful_photo.task;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.zieleeksw.eventful_photo.task.dto.TaskException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TaskException.class)
    public ResponseEntity<Map<String, String>> handleTaskException(TaskException e) {
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }
}