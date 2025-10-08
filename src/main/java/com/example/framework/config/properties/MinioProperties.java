package com.example.framework.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author: qinglong
 * @create-date: 2024/8/14 9:34
 */
@Data
@Component
@ConfigurationProperties(prefix = "upload.minio")
public class MinioProperties {

    private String url;
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;
}
