package pl.zieleeksw.eventful_photo.integration;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import pl.zieleeksw.eventful_photo.task.dto.StatusDto;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SaveMultipartFileTaskIntegrationTests extends BaseIntegration {

    @Test
    void shouldSaveTaskFromMultiPartFile() throws Exception {
        byte[] imageBytes = "image-data".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", imageBytes);

        MvcResult result = mockMvc.perform(multipart("/api/v1/tasks")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn();

        UUID id = UUID.fromString(JsonPath.read(result.getResponse().getContentAsString(), "$.id"));

        mockMvc.perform(get("/api/v1/tasks/{taskId}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.status").value(StatusDto.PENDING.toString()))
                .andExpect(jsonPath("$.detectedPersons").value(0));
    }

    @Test
    void shouldThrowTaskExceptionWhenFileIsEmpty() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", new byte[0]);

        mockMvc.perform(multipart("/api/v1/tasks")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("File is empty"));

        mockMvc.perform(get("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
