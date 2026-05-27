package org.example.Servlet;

import org.example.Entity.MediaEpisodes;
import org.example.Entity.Movie_details;
import org.example.Service.MediaEpisodesService;
import org.example.Service.TV_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class TV_Controller {

    @Autowired
    private TV_Service tvService;

    @Autowired
    private MediaEpisodesService mediaEpisodesService;

    /*-----------------------------------------------------------------*/
    /*                         动漫 /ANIME                              */
    /*-----------------------------------------------------------------*/

    @GetMapping("/ANIME/ALL")
    public List<Movie_details> animeAll() {
        return tvService.animeGetAll();
    }

    @GetMapping("/ANIME/ONEP")
    public List<Movie_details> animePage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "60") Integer pageSize) {
        return tvService.animeGetPage(page, pageSize);
    }

    @GetMapping("/ANIME/ONEID")
    public Movie_details animeById(@RequestParam("id") Long id) {
        return tvService.animeGetById(id);
    }

    @GetMapping("/ANIME/ONENAME")
    public List<Movie_details> animeSearch(@RequestParam("name") String name) {
        return tvService.animeSearchByName(name);
    }

    @GetMapping("/ANIME/FILTER")
    public List<Movie_details> animeFilter(
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "year", required = false) String year,
            @RequestParam(value = "region", required = false) String region,
            @RequestParam(value = "language", required = false) String language,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "60") Integer pageSize) {
        return tvService.animeFilter(type, year, region, language, sort, page, pageSize);
    }

    @GetMapping("/ANIME/EPISODES/ANIME")
    public MediaEpisodes animeEpisodes(@RequestParam("id") Long animeId) {
        return mediaEpisodesService.getByAnimeId(animeId);
    }

    @PostMapping("/ANIME/EPISODES/ADD")
    public String animeAddEpisodes(@RequestBody MediaEpisodes me) {
        int result = mediaEpisodesService.save(me);
        return result > 0 ? "添加成功" : "添加失败";
    }

    @PostMapping("/ANIME/EPISODES/UPDATE")
    public String animeUpdateEpisodes(@RequestBody MediaEpisodes me) {
        int result = mediaEpisodesService.modify(me);
        return result > 0 ? "更新成功" : "更新失败";
    }

    /*-----------------------------------------------------------------*/
    /*                        电视剧 /TV                                */
    /*-----------------------------------------------------------------*/

    @GetMapping("/TV/ALL")
    public List<Movie_details> tvAll() {
        return tvService.tvGetAll();
    }

    @GetMapping("/TV/ONEP")
    public List<Movie_details> tvPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "60") Integer pageSize) {
        return tvService.tvGetPage(page, pageSize);
    }

    @GetMapping("/TV/ONEID")
    public Movie_details tvById(@RequestParam("id") Long id) {
        return tvService.tvGetById(id);
    }

    @GetMapping("/TV/ONENAME")
    public List<Movie_details> tvSearch(@RequestParam("name") String name) {
        return tvService.tvSearchByName(name);
    }

    @GetMapping("/TV/FILTER")
    public List<Movie_details> tvFilter(
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "year", required = false) String year,
            @RequestParam(value = "region", required = false) String region,
            @RequestParam(value = "language", required = false) String language,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "60") Integer pageSize) {
        return tvService.tvFilter(type, year, region, language, sort, page, pageSize);
    }

    @GetMapping("/TV/EPISODES/TV")
    public MediaEpisodes tvEpisodes(@RequestParam("id") Long tvId) {
        return mediaEpisodesService.getByTvId(tvId);
    }

    @PostMapping("/TV/EPISODES/ADD")
    public String tvAddEpisodes(@RequestBody MediaEpisodes me) {
        int result = mediaEpisodesService.save(me);
        return result > 0 ? "添加成功" : "添加失败";
    }

    @PostMapping("/TV/EPISODES/UPDATE")
    public String tvUpdateEpisodes(@RequestBody MediaEpisodes me) {
        int result = mediaEpisodesService.modify(me);
        return result > 0 ? "更新成功" : "更新失败";
    }
}
