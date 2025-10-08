package com.example.framework.strategy.impl;

import com.example.framework.exception.BizException;
import com.example.framework.strategy.UploadStrategy;
import com.example.framework.utils.FileUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author: qinglong
 * @create-date: 2024/8/15 11:43
 */
@Service
public abstract class AbstractUploadStrategyImpl implements UploadStrategy {

    @Override
    public String uploadFile(MultipartFile file, String path) {
        try{
            String md5 = FileUtil.getMd5(file.getInputStream());
            // 获取文件后缀名
            String extName = FileUtil.getExtName(file.getOriginalFilename());
            String fileName = md5 + extName;
            if (!exists(path + fileName)) {
                upload(path, fileName, file.getInputStream());
            }
            return getFileAccessUrl(path + fileName);
        }catch (Exception e){
            e.printStackTrace();
            throw new BizException("文件上传失败");
        }
    }

    @Override
    public String uploadFile(String fileName, InputStream inputStream, String path) {
        try {
            upload(path,fileName,inputStream);
            return getFileAccessUrl(path + fileName);
        }catch(Exception e){
            e.printStackTrace();
            throw new BizException("文件上传失败");
        }
    }
    // 判断文件是否存在
    public abstract boolean exists(String filePath);

    // 上传文件
    public abstract void upload(String path,String fileName, InputStream inputStream) throws IOException;

    // 获取文件访问路径
    public abstract String getFileAccessUrl(String filePath);

}
