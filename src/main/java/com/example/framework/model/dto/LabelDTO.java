package com.example.framework.model.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author: qinglong
 * @create-date: 2024/5/27 13:55
 */
@Data
@Builder
public class LabelDTO {

    private String content;
    private String author;
}
