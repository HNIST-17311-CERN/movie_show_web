//package com.liujunming.DAO;
//
//import org.example.DAO.TV_DAO;
//import org.example.Entity.Movie_details;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//public class TV_DAOTest {
//
//    @Autowired
//    private TV_DAO tvDAO;
//
//    @Test
//    public void testFindAll() {
//        List<Movie_details> list = tvDAO.findAll();
//        System.out.println("===== 查询全部动漫 =====");
//        System.out.println("数量: " + list.size());
//        for (Movie_details m : list) {
//            System.out.println(m.getId() + " - " + m.getName() + " [" + m.getType() + "]");
//        }
//        assertNotNull(list);
//    }
//
//    @Test
//    public void testFindPage() {
//        List<Movie_details> list = tvDAO.findPage(1, 5);
//        System.out.println("===== 分页查询动漫（第1页，5条）=====");
//        for (Movie_details m : list) {
//            System.out.println(m.getId() + " - " + m.getName());
//        }
//        assertNotNull(list);
//        assertTrue(list.size() <= 5);
//    }
//
//    @Test
//    public void testFindById() {
//        List<Movie_details> all = tvDAO.findAll();
//        if (!all.isEmpty()) {
//            Long firstId = all.get(0).getId();
//            Movie_details m = tvDAO.findById(firstId);
//            System.out.println("===== 按ID查询动漫 =====");
//            System.out.println(m.getId() + " - " + m.getName());
//            assertNotNull(m);
//            assertEquals(firstId, m.getId());
//        } else {
//            System.out.println("===== 按ID查询动漫 =====");
//            System.out.println("无动漫数据，跳过测试");
//        }
//    }
//
//    @Test
//    public void testFindByName() {
//        List<Movie_details> list = tvDAO.findByName("世界");
//        System.out.println("===== 搜索动漫名称含'世界' =====");
//        for (Movie_details m : list) {
//            System.out.println(m.getId() + " - " + m.getName());
//        }
//        assertNotNull(list);
//    }
//
//    @Test
//    public void testFilter() {
//        List<Movie_details> list = tvDAO.filter(null, null, "日本", null, null, 1, 20);
//        System.out.println("===== 筛选日本动漫 =====");
//        System.out.println("数量: " + list.size());
//        for (Movie_details m : list) {
//            System.out.println(m.getId() + " - " + m.getName() + " [" + m.getRegion() + "]");
//        }
//        assertNotNull(list);
//    }
//}
