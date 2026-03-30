package org.example.Tool;


import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Component
public class FileUploadUtil
{

    public String get_file_url(MultipartFile file) throws IOException
    {
        if(file.isEmpty())
        {
            throw new RuntimeException("文件为空");
        }

        // 获取原文件名
        String OriginFileName = file.getOriginalFilename();

        // 生成唯一文件名
        String suffix = OriginFileName.substring(OriginFileName.lastIndexOf("."));
        String newFileName = System.currentTimeMillis() + suffix;//用毫秒级时间戳+后缀来生成文件名

        // 保存路径：项目 src/main/resources/filmlane-master/assets/images 文件夹
        String uploadDir = new File("src/main/resources/filmlane-master/assets/images").getAbsolutePath();
        File dest = new File(uploadDir, newFileName);

        // 保存文件
        file.transferTo(dest);

        String url = "src/main/resources/filmlane-master/assets/images/" + newFileName;
        return url;
    }

}
