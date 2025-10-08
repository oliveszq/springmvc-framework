package com.example.framework.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 发送邮箱的DTO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailDTO {

    private String email;

    private String subject;

    private Map<String, Object> commentMap;

    private String template;

}