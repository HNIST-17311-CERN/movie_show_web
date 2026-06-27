package org.example.Servlet;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
public class FileUploadController {

    private static final String UPLOAD_DIR = "uploads/files/";
    private static final long MAX_SIZE = 50 * 1024 * 1024; // 50MB

    /**
     * 上传附件，返回文件访问 URL
     */
    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) return Map.of("ok", false, "msg", "文件为空");
        if (file.getSize() > MAX_SIZE) return Map.of("ok", false, "msg", "文件不能超过 50MB");

        try {
            String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            Path dir = Paths.get(UPLOAD_DIR, dateDir);
            Files.createDirectories(dir);

            String originalName = file.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
            }

            String storedName = UUID.randomUUID() + ext;
            Path target = dir.resolve(storedName);
            file.transferTo(target);

            String url = "/api/files/download/" + dateDir + "/" + storedName
                    + "?name=" + URLEncoder.encode(originalName != null ? originalName : storedName, StandardCharsets.UTF_8);

            return Map.of("ok", true, "url", url, "name", originalName,
                    "size", file.getSize(), "type", file.getContentType(),
                    "storedName", storedName, "path", target.toAbsolutePath().toString());
        } catch (IOException e) {
            return Map.of("ok", false, "msg", "上传失败: " + e.getMessage());
        }
    }

    /**
     * 下载 / 预览附件
     */
    @GetMapping("/download/{dateDir}/{fileName}")
    public void download(@PathVariable String dateDir,
                         @PathVariable String fileName,
                         @RequestParam(value = "name", required = false) String displayName,
                         HttpServletResponse response) {
        Path filePath = Paths.get(UPLOAD_DIR, dateDir, fileName);
        if (!Files.exists(filePath)) {
            response.setStatus(404);
            return;
        }

        String downloadName = displayName != null ? displayName : fileName;
        try {
            String mime = Files.probeContentType(filePath);
            response.setContentType(mime != null ? mime : MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                    "inline; filename*=UTF-8''" + URLEncoder.encode(downloadName, StandardCharsets.UTF_8));
            Files.copy(filePath, response.getOutputStream());
        } catch (IOException e) {
            response.setStatus(500);
        }
    }

    /**
     * 列出已上传的文件
     */
    @GetMapping("/list")
    public List<Map<String, Object>> list() throws IOException {
        List<Map<String, Object>> files = new ArrayList<>();
        Path dir = Paths.get(UPLOAD_DIR);
        if (!Files.exists(dir)) return files;

        try (var stream = Files.walk(dir)) {
            var paths = stream.filter(p -> !Files.isDirectory(p)).toList();
            for (Path p : paths) {
                Map<String, Object> info = new HashMap<>();
                String rel = dir.relativize(p).toString().replace("\\", "/");
                info.put("name", p.getFileName().toString());
                info.put("url", "/api/files/download/" + rel);
                info.put("size", Files.size(p));
                info.put("time", Files.getLastModifiedTime(p).toString());
                files.add(info);
            }
        } catch (IOException ignored) {}
        return files;
    }
}
