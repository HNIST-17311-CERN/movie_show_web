package org.example.Entity;

import java.util.Date;

public class Movie_details {

    private Long id;                    // 鐢靛奖ID
    private String name;                // 鐢靛奖鍚嶇О
    private String cover;               // 灏侀潰鍥剧墖鍦板潃
    private String director;            // 瀵兼紨
    private String actors;              // 涓绘紨锛堝涓敤閫楀彿鍒嗛殧锛?
    private String type;                // 绫诲瀷锛堝墽鎯?绉戝够绛夛級
    private String region;              // 鍦板尯锛堝ぇ闄?缇庡浗绛夛級
    private String language;            // 璇█锛堝浗璇?鑻辫绛夛級
    private Date releaseDate;           // 涓婃槧鏃堕棿
    private Integer duration;           // 鐗囬暱锛堝垎閽燂級
    private String description;         // 鐢靛奖绠€浠?
    private Date createTime;            // 鍒涘缓鏃堕棿

    public Movie_details() {
    }

    public Movie_details(Long id, String name, String cover, String director,
            String actors, String type, String region, String language,
            Date releaseDate, Integer duration, String description, Date createTime) {
        this.id = id;
        this.name = name;
        this.cover = cover;
        this.director = director;
        this.actors = actors;
        this.type = type;
        this.region = region;
        this.language = language;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.description = description;
        this.createTime = createTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Movie_details{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cover='" + cover + '\'' +
                ", director='" + director + '\'' +
                ", actors='" + actors + '\'' +
                ", type='" + type + '\'' +
                ", region='" + region + '\'' +
                ", language='" + language + '\'' +
                ", releaseDate=" + releaseDate +
                ", duration=" + duration +
                ", description='" + description + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
