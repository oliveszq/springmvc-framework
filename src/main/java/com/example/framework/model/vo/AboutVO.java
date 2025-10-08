package com.example.framework.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AboutVO {

    @Schema(name = "About内容", description = "content", requiredMode = Schema.RequiredMode.REQUIRED , type = "String")
    private String content;
}
