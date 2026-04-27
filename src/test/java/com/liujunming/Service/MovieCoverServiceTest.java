package com.liujunming.Service;

import org.example.Service.Movie_Cover_URL_Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

@SpringBootTest
public class MovieCoverServiceTest
{

    @Autowired
    private Movie_Cover_URL_Service service;

    @Test
    public void testUploadCover() throws Exception
    {
        // 模拟图片文件
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "cover.jpg",
                "image/jpeg",
                "fake image data".getBytes()
        );

        String url = service.get_file_url(file);

        System.out.println("====== 封面上传测试 ======");
        System.out.println("封面URL: " + url);
    }
}