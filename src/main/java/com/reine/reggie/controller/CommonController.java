package com.reine.reggie.controller;

import com.reine.reggie.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传和下载
 *
 * @author reine
 * @since 2022/4/14 10:55
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    private final String resourcePath = ClassUtils.getDefaultClassLoader().getResource("").getPath() + "images/";

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        // file是一个临时文件，需要转存到指定位置
        log.info(file.toString());
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String fileName = UUID.randomUUID() + suffix;
        // 判断目录是否存在
        File dir = new File(resourcePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            String filePath = resourcePath + fileName;
            log.info("上传文件成功,存放地址为:{}", filePath);
            file.transferTo(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.success(fileName);
    }

    /**
     * 文件下载
     *
     * @param name     文件名
     * @param response 响应信息
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        // 输入流,读取文件内容
        try {
            FileInputStream fileInputStream = new FileInputStream(resourcePath + name);
            // 输出流,通过输出流将文件写回浏览器,在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");
            int len;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
            outputStream.close();
            fileInputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
