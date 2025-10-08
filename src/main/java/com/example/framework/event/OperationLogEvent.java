package com.example.framework.event;

import com.example.framework.entity.OperationLog;
import org.springframework.context.ApplicationEvent;

public class OperationLogEvent extends ApplicationEvent {

    public OperationLogEvent(OperationLog operationLog) {
        super(operationLog);
    }
}
