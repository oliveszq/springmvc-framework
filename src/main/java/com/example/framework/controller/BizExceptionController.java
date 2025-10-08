package com.example.framework.controller;

import com.example.framework.exception.BizException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: qinglong
 * @create-date: 2024/8/21 16:37
 */
@Tag(name = "业务层异常处理模块")
@RestController
public class BizExceptionController {

    @SneakyThrows
    @Operation(summary = "处理BizException")
    @RequestMapping("/bizException")
    public void handleBizException(HttpServletRequest request){
        if (request.getAttribute("bizException") instanceof BizException) {
            System.out.println(request.getAttribute("bizException"));
            throw ((BizException) request.getAttribute("bizException"));
        } else {
            throw new Exception();
        }
    }
}
