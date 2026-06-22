package org.example.Servlet;

import org.example.Service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/search")
    public Map<String, Object> search(
            @RequestParam("q") String q,
            @RequestParam(value = "mode", defaultValue = "2") int mode,
            @RequestParam(value = "type", defaultValue = "all") String type) {
        return searchService.search(q, mode, type);
    }
}
