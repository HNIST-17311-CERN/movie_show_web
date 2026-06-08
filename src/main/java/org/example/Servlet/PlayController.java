package org.example.Servlet;

import org.example.Entity.PlaySource;
import org.example.Service.PlaySourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/PLAY")
@CrossOrigin(origins = "*")
public class PlayController
{

    @Autowired
    public PlaySourceService playSourceService;

    // 视频文件根目录（相对于项目根目录）
    private static final String VIDEO_BASE = System.getProperty("user.dir");


    @GetMapping("/RESOURCES")//返回全部在线播放资源
    @CrossOrigin
    @PreAuthorize("hasAuthority('resource:view')")
    public List<PlaySource> get_all_resources()
    {
        return playSourceService.get_all();
    }

    @GetMapping("/RESOURCES/ONE")//根据电影ID查询播放资源
    @CrossOrigin
    @PreAuthorize("hasAuthority('resource:view')")
    public List<PlaySource> get_resource_by_movie_id(@RequestParam("id") Long id)
    {
        return playSourceService.get_by_movie_id(id);
    }

    @PostMapping("/RESOURCES/ADD")
    @CrossOrigin
    @PreAuthorize("hasAuthority('resource:manage')")
    public String add_resource(@RequestBody PlaySource resource)
    {
        int result = playSourceService.insert(resource);
        return result > 0 ? "播放资源添加成功" : "播放资源添加失败";
    }

    @PostMapping("/RESOURCES/UPDATE")
    @CrossOrigin
    @PreAuthorize("hasAuthority('resource:manage')")
    public String update_resource(@RequestBody PlaySource resource)
    {
        int result = playSourceService.update(resource);
        return result > 0 ? "播放资源更新成功" : "播放资源更新失败";
    }

    @PostMapping("/RESOURCES/DELETE")
    @CrossOrigin
    @PreAuthorize("hasAuthority('resource:manage')")
    public String delete_resource(@RequestParam("id") Long id)
    {
        int result = playSourceService.deleteById(id);
        return result > 0 ? "播放资源删除成功" : "播放资源删除失败";
    }


    /*----------------------------------------------------------------------------------------------*/
    // 视频流媒体（直接走 Servlet，稳定支持 Range）

    @GetMapping("/STREAM")
    @CrossOrigin
    @PreAuthorize("hasAuthority('resource:view')")
    public void stream(@RequestParam("id") Long id,
                       jakarta.servlet.http.HttpServletRequest request,
                       jakarta.servlet.http.HttpServletResponse response)
    {
        PlaySource source = playSourceService.get_by_id(id);
        if (source == null) {
            response.setStatus(404);
            return;
        }

        Path filePath = Paths.get(VIDEO_BASE, source.getUrl());
        File file = filePath.toFile();
        if (!file.exists()) {
            response.setStatus(404);
            return;
        }

        String mime = getMime(file.getName());
        long fileLength = file.length();
        String rangeHeader = request.getHeader("Range");

        try {
            if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
                // 解析 Range
                String rangeValue = rangeHeader.substring("bytes=".length());
                String[] parts = rangeValue.split("-");
                long start = Long.parseLong(parts[0].trim());
                long end = (parts.length > 1 && !parts[1].trim().isEmpty())
                        ? Long.parseLong(parts[1].trim())
                        : fileLength - 1;
                if (end >= fileLength) end = fileLength - 1;
                long contentLength = end - start + 1;

                response.setStatus(206);
                response.setContentType(mime);
                response.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);
                response.setHeader("Content-Length", String.valueOf(contentLength));
                response.setHeader("Accept-Ranges", "bytes");

                try (java.io.RandomAccessFile raf = new java.io.RandomAccessFile(file, "r");
                     java.io.OutputStream out = response.getOutputStream()) {
                    raf.seek(start);
                    byte[] buf = new byte[8192];
                    long remaining = contentLength;
                    while (remaining > 0) {
                        int toRead = (int) Math.min(buf.length, remaining);
                        int read = raf.read(buf, 0, toRead);
                        if (read == -1) break;
                        out.write(buf, 0, read);
                        remaining -= read;
                    }
                }
            } else {
                // 完整返回
                response.setContentType(mime);
                response.setHeader("Content-Length", String.valueOf(fileLength));
                response.setHeader("Accept-Ranges", "bytes");

                try (java.io.FileInputStream fis = new java.io.FileInputStream(file);
                     java.io.OutputStream out = response.getOutputStream()) {
                    byte[] buf = new byte[8192];
                    int read;
                    while ((read = fis.read(buf)) != -1) {
                        out.write(buf, 0, read);
                    }
                }
            }
        } catch (IOException e) {
            response.setStatus(500);
        }
    }

    private String getMime(String fileName) {
        String name = fileName.toLowerCase();
        if (name.endsWith(".mkv"))  return "video/x-matroska";
        if (name.endsWith(".mp4"))  return "video/mp4";
        if (name.endsWith(".webm")) return "video/webm";
        if (name.endsWith(".avi"))  return "video/x-msvideo";
        if (name.endsWith(".mov"))  return "video/quicktime";
        return "video/mp4";
    }

}
