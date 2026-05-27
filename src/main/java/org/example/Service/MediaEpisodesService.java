package org.example.Service;

import org.example.DAO.MediaEpisodesDAO;
import org.example.Entity.MediaEpisodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MediaEpisodesService {

    @Autowired
    private MediaEpisodesDAO mediaEpisodesDAO;

    public MediaEpisodes getByAnimeId(Long animeId) {
        return mediaEpisodesDAO.findByAnimeId(animeId);
    }

    public MediaEpisodes getByTvId(Long tvId) {
        return mediaEpisodesDAO.findByTvId(tvId);
    }

    public int save(MediaEpisodes me) {
        return mediaEpisodesDAO.insert(me);
    }

    public int modify(MediaEpisodes me) {
        return mediaEpisodesDAO.update(me);
    }

    public int remove(Long id) {
        return mediaEpisodesDAO.deleteById(id);
    }
}
