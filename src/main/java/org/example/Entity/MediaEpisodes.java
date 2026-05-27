package org.example.Entity;

public class MediaEpisodes {

    private Long id;
    private Long tvId;
    private Long animeId;
    private Integer totalEpisodes;
    private String updateStatus;

    public MediaEpisodes() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTvId() { return tvId; }
    public void setTvId(Long tvId) { this.tvId = tvId; }

    public Long getAnimeId() { return animeId; }
    public void setAnimeId(Long animeId) { this.animeId = animeId; }

    public Integer getTotalEpisodes() { return totalEpisodes; }
    public void setTotalEpisodes(Integer totalEpisodes) { this.totalEpisodes = totalEpisodes; }

    public String getUpdateStatus() { return updateStatus; }
    public void setUpdateStatus(String updateStatus) { this.updateStatus = updateStatus; }
}
