package org.example.Service;

import org.example.Tool.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class Movie_Cover_URL_Service
{
    @Autowired
    public FileUploadUtil fileUploadUtil;

    public String get_file_url(MultipartFile file) throws IOException
    {
        return fileUploadUtil.get_file_url(file);//存储海报文件并返回存储海报的URL
    }

}
