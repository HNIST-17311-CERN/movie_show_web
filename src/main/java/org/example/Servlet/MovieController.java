package org.example.Servlet;

import org.example.DAO.Movie_ScoreDAO;
import org.example.Entity.Movie_Resource;
import org.example.Entity.Movie_Score;
import org.example.Entity.Movie_details;
import org.example.Entity.User;
import org.example.Service.MovieService;
import org.example.Service.Movie_Cover_URL_Service;
import org.example.Service.Movie_Resource_Service;
import org.example.Service.Movie_Score_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.AccessType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/FILMES")
@CrossOrigin(origins = "*")  // 允许所有来源访问
public class MovieController
{

    @Autowired
    public MovieService movieService;

    @Autowired
    public Movie_Resource_Service movieResourceService;

    @Autowired
    public Movie_Score_Service movieScoreService;

    @Autowired
    public Movie_Cover_URL_Service movieCoverUrlService;


    @GetMapping("/ALL")//返回所有电影
    @CrossOrigin // 允许跨域
    public List<Movie_details> get_all_filmes()
    {
        return movieService.get_all();
    }

    @GetMapping("/ONEP")//返回一页电影
    @CrossOrigin // 允许跨域
    public List<Movie_details> get_onep_filmes()
    {
        return movieService.get_one_p(1,60);
    }

    @GetMapping("/ONEID")//根据id查询电影
    @CrossOrigin // 允许跨域
    public Movie_details ger_one_details_by_id(@RequestParam("id")int id)
    {
        return movieService.get_one_details(id);
    }

    @GetMapping("/ONENAME")//根据id查询电影
    @CrossOrigin // 允许跨域
    public List<Movie_details> search_movie(@RequestParam("name") String name)
    {
        return movieService.search_by_name(name);
    }

    @PostMapping("/ADD")//添加电影
    @CrossOrigin // 允许跨域
    public String add_movie(@RequestPart("movie") Movie_details movie,@RequestPart("file") MultipartFile file) throws IOException//前端传的JSON，前端上传的图片
    {
            // 1. 上传图片 -> 得到 URL
            String url = movieCoverUrlService.get_file_url(file);
            movie.setCover(url); // 设置电影封面 URL

            // 2. 调用 Service 保存电影
            boolean result = movieService.add_movie(movie);
            return result ? "添加成功" : "添加失败";
    }

    @PostMapping("/UPDATE")//更新电影
    @CrossOrigin // 允许跨域
    public String update_movie(@RequestPart("movie") Movie_details movie,@RequestPart("file") MultipartFile file) throws IOException
    {
        // 1. 上传图片 -> 得到 URL
        String url = movieCoverUrlService.get_file_url(file);
        movie.setCover(url); // 设置电影封面 URL

        boolean result = movieService.update_movie(movie);
        return result ? "更新成功" : "更新失败";
    }

    @PostMapping("/DELETE")//删除电影
    @CrossOrigin // 允许跨域
    public String delete_movie(@RequestParam("id") int id)
    {
        boolean result = movieService.delete_movie(id);
        return result ? "删除成功" : "删除失败";
    }


    /*----------------------------------------------------------------------------------------------*/


    @GetMapping("/SCORE/ONE")//根据id查询电影分数
    @CrossOrigin // 允许跨域
    public List<Movie_Score> get_score_by_id(@RequestParam("id")Long id)
    {
        return movieScoreService.get_score_by_id(id);
    }


    /*----------------------------------------------------------------------------------------------*/

    @GetMapping("/RESOURCE/ONE")//根据id查询电影资源
    @CrossOrigin // 允许跨域
    public List<Movie_Resource> get_resource_by_id(@RequestParam("id")Long id)
    {
        return movieResourceService.get_resource_by_id(id);
    }




}


