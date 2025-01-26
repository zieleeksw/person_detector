package pl.zieleeksw.eventful_photo.integration;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import pl.zieleeksw.eventful_photo.task.dto.StatusDto;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class URLFileSaveIntegrationTest extends BaseIntegration {

    @Test
    void shouldSaveTaskFromUrl() throws Exception {
        String validUrl = "https://www.kasandbox.org/programming-images/avatars/spunky-sam-green.png";

        MvcResult result = mockMvc.perform(post("/api/v1/tasks/url")
                        .param("url", validUrl)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
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
    void shouldThrowTaskExceptionWhenUrlIsInvalid() throws Exception {
        String invalidUrl = "http://invalid-url.com/image.jpg";

        String expectedError = "Failed to download image from URL: Error occurred while fetching image: Failed to fetch image, HTTP status: 403";
        mockMvc.perform(post("/api/v1/tasks/url")
                        .param("url", invalidUrl)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(expectedError));

        mockMvc.perform(get("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
