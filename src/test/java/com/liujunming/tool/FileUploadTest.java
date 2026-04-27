package com.liujunming.tool;

import org.example.Tool.FileUploadUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

@SpringBootTest
public class FileUploadTest
{

    @Autowired
    private FileUploadUtil fileUploadUtil;

    @Test
    public void testUpload() throws Exception
    {
        // 模拟文件
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "fake image content".getBytes()
        );

        String url = fileUploadUtil.get_file_url(file);

        System.out.println("====== 文件上传测试 ======");
        System.out.println("访问路径: " + url);
    }
}