package org.example.Servlet;

import org.example.Entity.ImageSearchResult;
import org.example.Service.ImageSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ImageSearchController {

    @Autowired
    private ImageSearchService imageSearchService;

    @PostMapping("/image-search")
    public ImageSearchResult imageSearch(@RequestParam("image") MultipartFile image) {
        try {
            return imageSearchService.search(image);
        } catch (Exception e) {
            throw new RuntimeException("图片识别失败: " + e.getMessage(), e);
        }
    }
}
