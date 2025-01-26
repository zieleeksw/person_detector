package pl.zieleeksw.eventful_photo.task.integration;


import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import pl.zieleeksw.eventful_photo.task.dto.StatusDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetTasksIntegrationTests extends BaseIntegration {

    @Test
    void shouldGetTasks() throws Exception {
        byte[] imageBytes = "image-data".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", imageBytes);

        mockMvc.perform(multipart("/api/v1/tasks")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn();

        mockMvc.perform(multipart("/api/v1/tasks")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn();

        mockMvc.perform(get("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").isNotEmpty())
                .andExpect(jsonPath("$[0].status").value(StatusDto.PENDING.toString()))
                .andExpect(jsonPath("$[0].detectedPersons").value(0))
                .andExpect(jsonPath("$[1].id").isNotEmpty())
                .andExpect(jsonPath("$[1].status").value(StatusDto.PENDING.toString()))
                .andExpect(jsonPath("$[1].detectedPersons").value(0));
    }

    @Test
    void shouldGetTasksOnEmptyDb() throws Exception {
        mockMvc.perform(get("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
