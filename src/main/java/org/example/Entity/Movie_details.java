package org.example.Entity;

import java.util.Date;

public class Movie_details {

    private Long id;                    // 电影ID
    private String name;                // 电影名称
    private String cover;               // 封面图片地址
    private String director;            // 导演
    private String actors;              // 主演（多个用逗号分隔）
    private String type;                // 类型（剧情/科幻等）
    private String region;              // 地区（大陆/美国等）
    private String language;            // 语言（国语/英语等）
    private Date releaseDate;           // 上映时间
    private Integer duration;           // 片长（分钟）
    private String description;         // 电影简介
    private Date createTime;            // 创建时间

    // 无参构造方法
    public Movie_details() {
    }

    // 全参构造方法
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

    // Getter 和 Setter 方法
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

    // toString 方法（可选）
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