package org.example.Entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Movie_Score {

    private Long id;          // 评分ID
    private Long movieId;     // 电影ID
    private String source;    // 评分来源（douban=豆瓣 / imdb=IMDb）
    private BigDecimal score; // 评分（如7.2）
    private Integer count;    // 评分人数

    // 无参构造
    public Movie_Score() {
    }

    // 全参构造
    public Movie_Score(Long id, Long movieId, String source, BigDecimal score, Integer count) {
        this.id = id;
        this.movieId = movieId;
        this.source = source;
        this.score = score;
        this.count = count;
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "Movie_Score{" +
                "id=" + id +
                ", movieId=" + movieId +
                ", source='" + source + '\'' +
                ", score=" + score +
                ", count=" + count +
                '}';
    }
}