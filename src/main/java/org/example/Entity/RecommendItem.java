package org.example.Entity;

import java.sql.Timestamp;

public class RecommendItem {
    private Long movieId;
    private String movieName;
    private String cover;
    private int sortOrder;
    private Timestamp createTime;

    public RecommendItem() {}

    public RecommendItem(Long movieId, String movieName, String cover, int sortOrder, Timestamp createTime) {
        this.movieId = movieId;
        this.movieName = movieName;
        this.cover = cover;
        this.sortOrder = sortOrder;
        this.createTime = createTime;
    }

    public Long getMovieId() { return movieId; }
    public void setMovieId(Long movieId) { this.movieId = movieId; }

    public String getMovieName() { return movieName; }
    public void setMovieName(String movieName) { this.movieName = movieName; }

    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }

    public Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(Timestamp createTime) { this.createTime = createTime; }
}
