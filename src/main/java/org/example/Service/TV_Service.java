package org.example.Service;

import org.example.DAO.TV_DAO;
import org.example.Entity.Movie_details;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TV_Service {

    @Autowired
    private TV_DAO tvDAO;

    /*-----------------------------------------------------------------*/
    /*                         动漫                                     */
    /*-----------------------------------------------------------------*/

    public List<Movie_details> animeGetAll() { return tvDAO.animeFindAll(); }
    public List<Movie_details> animeGetPage(int page, int size) { return tvDAO.animeFindPage(page, size); }
    public Movie_details animeGetById(Long id) { return tvDAO.animeFindById(id); }
    public List<Movie_details> animeSearchByName(String name) { return tvDAO.animeFindByName(name); }
    public List<Movie_details> animeFilter(String type, String year, String region,
                                            String language, String sort, int page, int size) {
        return tvDAO.animeFilter(type, year, region, language, sort, page, size);
    }

    /*-----------------------------------------------------------------*/
    /*                        电视剧                                    */
    /*-----------------------------------------------------------------*/

    public List<Movie_details> tvGetAll() { return tvDAO.tvFindAll(); }
    public List<Movie_details> tvGetPage(int page, int size) { return tvDAO.tvFindPage(page, size); }
    public Movie_details tvGetById(Long id) { return tvDAO.tvFindById(id); }
    public List<Movie_details> tvSearchByName(String name) { return tvDAO.tvFindByName(name); }
    public List<Movie_details> tvFilter(String type, String year, String region,
                                         String language, String sort, int page, int size) {
        return tvDAO.tvFilter(type, year, region, language, sort, page, size);
    }
}
