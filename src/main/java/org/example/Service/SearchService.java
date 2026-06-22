package org.example.Service;

import org.example.DAO.SearchDAO;
import org.example.Entity.Movie_details;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {

    @Autowired
    private SearchDAO searchDAO;

    private static final String[] TYPES = {"all", "movie", "tv", "anime"};

    public Map<String, Object> search(String q, int mode, String type) {
        if (q == null || q.trim().isEmpty()) return Map.of();
        if (mode != 2 && mode != 3) mode = 2;
        if (type == null || !List.of(TYPES).contains(type)) type = "all";

        List<Movie_details> results = searchDAO.search(q.trim(), mode, type);

        // 统计各类型数量
        long movieCount = results.stream().filter(r -> !isAnime(r) && !isTv(r)).count();
        long tvCount = results.stream().filter(this::isTv).count();
        long animeCount = results.stream().filter(this::isAnime).count();

        Map<String, Object> resp = new HashMap<>();
        resp.put("results", results);
        resp.put("counts", Map.of(
                "all", results.size(),
                "movie", movieCount,
                "tv", tvCount,
                "anime", animeCount,
                "种子", 0,
                "网盘", 0
        ));
        return resp;
    }

    private boolean isAnime(Movie_details m) {
        return m.getType() != null && m.getType().contains("动漫");
    }

    private boolean isTv(Movie_details m) {
        return m.getType() != null && m.getType().contains("电视剧");
    }
}
