package org.example.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.example.Entity.ImageSearchResult;
import org.example.Entity.Movie_details;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageSearchService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    private static final String PYTHON_URL = "http://localhost:8085/movie/recognize";

    @Autowired
    private MovieService movieService;

    public ImageSearchResult search(MultipartFile image) throws Exception {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", image.getResource());
        body.add("text", "请识别这张图片中的电影");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                PYTHON_URL, HttpMethod.POST, request, String.class);

        ImageSearchResult result = objectMapper.readValue(response.getBody(), ImageSearchResult.class);
        enrichWithMovieDetails(result);
        return result;
    }

    private void enrichWithMovieDetails(ImageSearchResult result) {
        ImageSearchResult.MovieGuess guess = result.getMovieGuess();
        if (guess == null) return;

        // 补充匹配电影详情
        ImageSearchResult.MovieMatched matched = guess.getMatchedMovie();
        if (matched != null && matched.getId() != null) {
            fillMovieDetail(matched);
        }

        // 补充相似电影详情
        if (guess.getSimilarMovies() != null) {
            for (ImageSearchResult.MovieMatched m : guess.getSimilarMovies()) {
                if (m.getId() != null) fillMovieDetail(m);
            }
        }
    }

    private void fillMovieDetail(ImageSearchResult.MovieMatched m) {
        try {
            Movie_details d = movieService.get_one_details(m.getId().intValue());
            if (d != null) {
                m.setCover(d.getCover());
                if (m.getName() == null || m.getName().isEmpty()) m.setName(d.getName());
                if (m.getDirector() == null || m.getDirector().isEmpty()) m.setDirector(d.getDirector());
                if (m.getType() == null || m.getType().isEmpty()) m.setType(d.getType());
                if (m.getReleaseDate() == null || m.getReleaseDate().isEmpty()) {
                    m.setReleaseDate(d.getReleaseDate() != null ? d.getReleaseDate().toString() : null);
                }
                if (m.getDescription() == null || m.getDescription().isEmpty()) m.setDescription(d.getDescription());
            }
        } catch (Exception e) {
            System.err.println("[ImageSearch] 获取电影详情失败 id=" + m.getId() + ": " + e.getMessage());
        }
    }
}
