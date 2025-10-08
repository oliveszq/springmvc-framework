package com.example.framework.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

import static com.example.framework.enums.ZoneEnum.SHANGHAI;


@Configuration
public class GlobalZoneConfig {

    //Bean初始化完成后自动调用该方法
    @PostConstruct
    public void setGlobalZone() {
        //设置默认时区
        TimeZone.setDefault(TimeZone.getTimeZone(SHANGHAI.getZone()));
    }

}
