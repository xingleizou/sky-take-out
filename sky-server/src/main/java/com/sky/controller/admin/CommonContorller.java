package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * 通用接口
 */
@RestController
@RequestMapping("/admin/common")
@Slf4j
public class CommonContorller {

    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @RequestMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传：{}", file);

        try {
            // 获取原始文件名
            String originalFilename = file.getOriginalFilename();

            // 获取文件扩展名
            String extension = null;
            if (originalFilename != null) {
                int lastDotIndex = originalFilename.lastIndexOf(".");
                if (lastDotIndex > 0) {
                    extension = originalFilename.substring(lastDotIndex);
                }
            }

            // 生成唯一的文件名，防止文件名冲突
            String objectName = UUID.randomUUID().toString() + extension;

            // 调用AliOssUtil工具类上传文件
            String url = aliOssUtil.upload(file.getBytes(), objectName);

            // 返回上传成功的文件访问路径
            return Result.success(url);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败");
        }
    }

}
