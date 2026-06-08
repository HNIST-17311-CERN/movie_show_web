package org.example.Entity;

import java.time.LocalDateTime;

public class PlaySource {

    private Long id;
    private Long movieId;
    private String url;
    private String name;
    private LocalDateTime createTime;

    public PlaySource() {
    }

    public PlaySource(Long id, Long movieId, String url, String name, LocalDateTime createTime) {
        this.id = id;
        this.movieId = movieId;
        this.url = url;
        this.name = name;
        this.createTime = createTime;
    }

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "PlaySource{" +
                "id=" + id +
                ", movieId=" + movieId +
                ", url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
