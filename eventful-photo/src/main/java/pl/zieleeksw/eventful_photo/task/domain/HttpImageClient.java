package pl.zieleeksw.eventful_photo.task.domain;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

class HttpImageClient {

    byte[] download(String imageUrl) throws IOException {
        URI uri = URI.create(imageUrl);
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();

            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() != 200) {
                throw new IOException(String.format("Failed to fetch image, HTTP status: %d", response.statusCode()));
            }

            return response.body();
        } catch (IllegalArgumentException | IOException | InterruptedException e) {
            throw new IOException("Error occurred while fetching image: " + e.getMessage());
        }
    }
}
