package org.example.Entity;

import java.time.LocalDateTime;

public class Movie_Resource {

    private Long id;             // 资源ID
    private Long movieId;        // 电影ID
    private String name;         // 资源名称
    private String quality;      // 清晰度（1080p / 4K）
    private String size;         // 文件大小（如2.9GB）
    private String type;         // 资源类型（磁力 / 网盘）
    private String url;          // 下载地址（磁力链接或网盘链接）
    private LocalDateTime createTime; // 发布时间
    private String subtitle;     // 字幕类型（中字 / 英文 / 无字幕）

    // 无参构造
    public Movie_Resource() {
    }

    // 全参构造
    public Movie_Resource(Long id, Long movieId, String name, String quality, String size,
            String type, String url, LocalDateTime createTime, String subtitle) {
        this.id = id;
        this.movieId = movieId;
        this.name = name;
        this.quality = quality;
        this.size = size;
        this.type = type;
        this.url = url;
        this.createTime = createTime;
        this.subtitle = subtitle;
    }

    // getter & setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    @Override
    public String toString() {
        return "Movie_Resource{" +
                "id=" + id +
                ", movieId=" + movieId +
                ", name='" + name + '\'' +
                ", quality='" + quality + '\'' +
                ", size='" + size + '\'' +
                ", type='" + type + '\'' +
                ", url='" + url + '\'' +
                ", createTime=" + createTime +
                ", subtitle='" + subtitle + '\'' +
                '}';
    }
}