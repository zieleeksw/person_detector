package pl.zieleeksw.eventful_photo.integration;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.SelectorResolutionResult;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import pl.zieleeksw.eventful_photo.task.dto.StatusDto;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetTaskCountByStatusIntegrationTests extends BaseIntegration {

    @Test
    void shouldReturnTaskCountByStatus() throws Exception {
        byte[] imageBytes = "image-data".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", imageBytes);

        // status pending
        mockMvc.perform(multipart("/api/v1/tasks")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn();

        MvcResult result2 = mockMvc.perform(multipart("/api/v1/tasks")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn();

        UUID id2 = UUID.fromString(JsonPath.read(result2.getResponse().getContentAsString(), "$.id"));

        mockMvc.perform(put("/api/v1/tasks/{id}/status", id2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(StatusDto.COMPLETED.toString()))
                .andExpect(status().isOk());

        MvcResult result3 = mockMvc.perform(multipart("/api/v1/tasks")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn();

        UUID id3 = UUID.fromString(JsonPath.read(result3.getResponse().getContentAsString(), "$.id"));

        mockMvc.perform(put("/api/v1/tasks/{id}/status", id3)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SelectorResolutionResult.Status.FAILED.toString()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/tasks/count")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.PENDING").value(1))
                .andExpect(jsonPath("$.COMPLETED").value(1))
                .andExpect(jsonPath("$.FAILED").value(1))
                .andExpect(jsonPath("$.IN_PROGRESS").value(0));
    }

    @Test
    void shouldReturnTaskCountByStatusOnEmptyDb() throws Exception {
        mockMvc.perform(get("/api/v1/tasks/count")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.PENDING").value(0))
                .andExpect(jsonPath("$.COMPLETED").value(0))
                .andExpect(jsonPath("$.FAILED").value(0))
                .andExpect(jsonPath("$.IN_PROGRESS").value(0));

    }
}
