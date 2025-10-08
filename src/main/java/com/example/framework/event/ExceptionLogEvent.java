package com.example.framework.event;

import com.example.framework.entity.ExceptionLog;
import org.springframework.context.ApplicationEvent;

public class ExceptionLogEvent extends ApplicationEvent {
    public ExceptionLogEvent(ExceptionLog exceptionLog) {
        super(exceptionLog);
    }
}
