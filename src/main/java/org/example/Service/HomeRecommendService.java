package org.example.Service;

import org.example.DAO.HomeRecommendDAO;
import org.example.Entity.Movie_details;
import org.example.Entity.RecommendItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HomeRecommendService {

    @Autowired
    private HomeRecommendDAO homeRecommendDAO;

    /*==========================================================================
     *                              电影推荐
     *==========================================================================*/

    public List<RecommendItem> getMovieItems() {
        return homeRecommendDAO.findMovieItems();
    }

    public List<Movie_details> getMovieRecommend(int limit) {
        return homeRecommendDAO.findMovieRecommend(limit);
    }

    public int addMovieRecommend(Long movieId, int sortOrder) {
        return homeRecommendDAO.insertMovieRecommend(movieId, sortOrder);
    }

    public int deleteMovieRecommend(Long movieId) {
        return homeRecommendDAO.deleteMovieRecommend(movieId);
    }

    public int updateMovieSortOrder(Long movieId, int sortOrder) {
        return homeRecommendDAO.updateMovieSortOrder(movieId, sortOrder);
    }

    /*==========================================================================
     *                              剧集推荐
     *==========================================================================*/

    public List<RecommendItem> getTvItems() {
        return homeRecommendDAO.findTvItems();
    }

    public List<Movie_details> getTvRecommend(int limit) {
        return homeRecommendDAO.findTvRecommend(limit);
    }

    public int addTvRecommend(Long movieId, int sortOrder) {
        return homeRecommendDAO.insertTvRecommend(movieId, sortOrder);
    }

    public int deleteTvRecommend(Long movieId) {
        return homeRecommendDAO.deleteTvRecommend(movieId);
    }

    public int updateTvSortOrder(Long movieId, int sortOrder) {
        return homeRecommendDAO.updateTvSortOrder(movieId, sortOrder);
    }

    /*==========================================================================
     *                              动漫推荐
     *==========================================================================*/

    public List<RecommendItem> getAnimeItems() {
        return homeRecommendDAO.findAnimeItems();
    }

    public List<Movie_details> getAnimeRecommend(int limit) {
        return homeRecommendDAO.findAnimeRecommend(limit);
    }

    public int addAnimeRecommend(Long movieId, int sortOrder) {
        return homeRecommendDAO.insertAnimeRecommend(movieId, sortOrder);
    }

    public int deleteAnimeRecommend(Long movieId) {
        return homeRecommendDAO.deleteAnimeRecommend(movieId);
    }

    public int updateAnimeSortOrder(Long movieId, int sortOrder) {
        return homeRecommendDAO.updateAnimeSortOrder(movieId, sortOrder);
    }
}
