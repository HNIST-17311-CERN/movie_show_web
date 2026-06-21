package org.example.Servlet;

import org.example.Entity.Movie_details;
import org.example.Entity.RecommendItem;
import org.example.Service.HomeRecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/home")
@CrossOrigin(origins = "*")
public class HomeRecommendController {

    @Autowired
    private HomeRecommendService homeRecommendService;

    /*==========================================================================
     *                              电影推荐
     *==========================================================================*/

    @GetMapping("/movie")
    @PreAuthorize("hasAuthority('movie:view')")
    public List<Movie_details> movieRecommend(
            @RequestParam(value = "limit", defaultValue = "48") int limit) {
        return homeRecommendService.getMovieRecommend(limit);
    }

    @GetMapping("/movie/items")
    @PreAuthorize("hasAuthority('movie:view')")
    public List<RecommendItem> movieItems() {
        return homeRecommendService.getMovieItems();
    }

    @PostMapping("/movie/add")
    @PreAuthorize("hasAuthority('movie:manage')")
    public String addMovieRecommend(
            @RequestParam("movieId") Long movieId,
            @RequestParam(value = "sortOrder", defaultValue = "0") int sortOrder) {
        int rows = homeRecommendService.addMovieRecommend(movieId, sortOrder);
        return rows > 0 ? "电影推荐添加成功" : "电影推荐添加失败";
    }

    @PostMapping("/movie/delete")
    @PreAuthorize("hasAuthority('movie:manage')")
    public String deleteMovieRecommend(@RequestParam("movieId") Long movieId) {
        int rows = homeRecommendService.deleteMovieRecommend(movieId);
        return rows > 0 ? "电影推荐删除成功" : "电影推荐删除失败";
    }

    @PostMapping("/movie/reorder")
    @PreAuthorize("hasAuthority('movie:manage')")
    public String reorderMovieRecommend(
            @RequestParam("movieId") Long movieId,
            @RequestParam("sortOrder") int sortOrder) {
        int rows = homeRecommendService.updateMovieSortOrder(movieId, sortOrder);
        return rows > 0 ? "电影推荐排序更新成功" : "电影推荐排序更新失败";
    }

    /*==========================================================================
     *                              剧集推荐
     *==========================================================================*/

    @GetMapping("/tv")
    @PreAuthorize("hasAuthority('movie:view')")
    public List<Movie_details> tvRecommend(
            @RequestParam(value = "limit", defaultValue = "48") int limit) {
        return homeRecommendService.getTvRecommend(limit);
    }

    @GetMapping("/tv/items")
    @PreAuthorize("hasAuthority('movie:view')")
    public List<RecommendItem> tvItems() {
        return homeRecommendService.getTvItems();
    }

    @PostMapping("/tv/add")
    @PreAuthorize("hasAuthority('movie:manage')")
    public String addTvRecommend(
            @RequestParam("movieId") Long movieId,
            @RequestParam(value = "sortOrder", defaultValue = "0") int sortOrder) {
        int rows = homeRecommendService.addTvRecommend(movieId, sortOrder);
        return rows > 0 ? "剧集推荐添加成功" : "剧集推荐添加失败";
    }

    @PostMapping("/tv/delete")
    @PreAuthorize("hasAuthority('movie:manage')")
    public String deleteTvRecommend(@RequestParam("movieId") Long movieId) {
        int rows = homeRecommendService.deleteTvRecommend(movieId);
        return rows > 0 ? "剧集推荐删除成功" : "剧集推荐删除失败";
    }

    @PostMapping("/tv/reorder")
    @PreAuthorize("hasAuthority('movie:manage')")
    public String reorderTvRecommend(
            @RequestParam("movieId") Long movieId,
            @RequestParam("sortOrder") int sortOrder) {
        int rows = homeRecommendService.updateTvSortOrder(movieId, sortOrder);
        return rows > 0 ? "剧集推荐排序更新成功" : "剧集推荐排序更新失败";
    }

    /*==========================================================================
     *                              动漫推荐
     *==========================================================================*/

    @GetMapping("/anime")
    @PreAuthorize("hasAuthority('movie:view')")
    public List<Movie_details> animeRecommend(
            @RequestParam(value = "limit", defaultValue = "48") int limit) {
        return homeRecommendService.getAnimeRecommend(limit);
    }

    @GetMapping("/anime/items")
    @PreAuthorize("hasAuthority('movie:view')")
    public List<RecommendItem> animeItems() {
        return homeRecommendService.getAnimeItems();
    }

    @PostMapping("/anime/add")
    @PreAuthorize("hasAuthority('movie:manage')")
    public String addAnimeRecommend(
            @RequestParam("movieId") Long movieId,
            @RequestParam(value = "sortOrder", defaultValue = "0") int sortOrder) {
        int rows = homeRecommendService.addAnimeRecommend(movieId, sortOrder);
        return rows > 0 ? "动漫推荐添加成功" : "动漫推荐添加失败";
    }

    @PostMapping("/anime/delete")
    @PreAuthorize("hasAuthority('movie:manage')")
    public String deleteAnimeRecommend(@RequestParam("movieId") Long movieId) {
        int rows = homeRecommendService.deleteAnimeRecommend(movieId);
        return rows > 0 ? "动漫推荐删除成功" : "动漫推荐删除失败";
    }

    @PostMapping("/anime/reorder")
    @PreAuthorize("hasAuthority('movie:manage')")
    public String reorderAnimeRecommend(
            @RequestParam("movieId") Long movieId,
            @RequestParam("sortOrder") int sortOrder) {
        int rows = homeRecommendService.updateAnimeSortOrder(movieId, sortOrder);
        return rows > 0 ? "动漫推荐排序更新成功" : "动漫推荐排序更新失败";
    }
}
