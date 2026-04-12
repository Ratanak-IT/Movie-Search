package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Movie;
import model.MovieResponse;
import model.Video;
import model.VideoResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class MovieServiceImpl implements MovieService {

    private static final String BASE_URL = "https://api.themoviedb.org/3";
    private static final String ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJlYmJkZTdjYTQyYWQyMGMzOTg4NTUxYjRmNjJjM2E0OCIsIm5iZiI6MTc3NjAwNzAzMS4yODE5OTk4LCJzdWIiOiI2OWRiYjc3NzJjYzVkMWZiOGQzNTg0ZmYiLCJzY29wZXMiOlsiYXBpX3JlYWQiXSwidmVyc2lvbiI6MX0.4MF9JwA9X4vwHtzfqovv7WnqiCXuU72LWOIwzW_wfUc";

    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static final ObjectMapper mapper = new ObjectMapper();

    private HttpRequest buildRequest(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .header("Accept", "application/json")
                .GET()
                .build();
    }

    @Override
    public MovieResponse searchMovies(String query, int page) {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = String.format("%s/search/movie?query=%s&page=%d", BASE_URL, encodedQuery, page);

        HttpRequest request = buildRequest(url);

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return mapper.readValue(response.body(), MovieResponse.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to search movies: " + e.getMessage());
        }
    }

    @Override
    public Movie getMovieDetail(int movieId) {
        String url = String.format("%s/movie/%d", BASE_URL, movieId);
        HttpRequest request = buildRequest(url);

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return mapper.readValue(response.body(), Movie.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to get movie detail: " + e.getMessage());
        }
    }

    @Override
    public String getTrailerUrl(int movieId) {
        String url = String.format("%s/movie/%d/videos", BASE_URL, movieId);
        HttpRequest request = buildRequest(url);

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            VideoResponse videoResponse = mapper.readValue(response.body(), VideoResponse.class);

            if (videoResponse.getResults() == null || videoResponse.getResults().isEmpty()) {
                return null;
            }

            // Find first YouTube trailer
            return videoResponse.getResults().stream()
                    .filter(v -> "YouTube".equalsIgnoreCase(v.getSite())
                            && "Trailer".equalsIgnoreCase(v.getType()))
                    .map(v -> "https://www.youtube.com/watch?v=" + v.getKey())
                    .findFirst()
                    .orElse(
                        // Fallback: any YouTube video
                        videoResponse.getResults().stream()
                            .filter(v -> "YouTube".equalsIgnoreCase(v.getSite()))
                            .map(v -> "https://www.youtube.com/watch?v=" + v.getKey())
                            .findFirst()
                            .orElse(null)
                    );
        } catch (IOException | InterruptedException e) {
            return null;
        }
    }
}
