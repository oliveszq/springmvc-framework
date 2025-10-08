package com.example.framework.strategy.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

/**
 * @author: qinglong
 * @create-date: 2024/8/19 10:00
 */
@Service("localUploadStrategyImpl")
public class LocalUploadStrategyImpl extends AbstractUploadStrategyImpl{

    @Value("${upload.local}")
    private String localPath;

    @Override
    public boolean exists(String filePath) {
        boolean exist = true;
        File file = new File(localPath + filePath);
        if (!file.exists()){
            exist = false;
        }
        return exist;
    }

    @Override
    public void upload(String path, String fileName, InputStream inputStream) throws IOException {
        File file = new File(localPath + path + fileName);
        try (OutputStream outputStream = Files.newOutputStream(file.toPath())) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    @Override
    public String getFileAccessUrl(String filePath) {
        if (exists(filePath)){
            return localPath + filePath;
        }
        return null;
    }
}
