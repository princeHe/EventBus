package com.example.he.eventbus;

import java.lang.reflect.Method;

/**
 * Created by he on 2017/5/14.
 */

public class SubscribeMethod {
    private Method method;
    private ThreadMode threadMode;
    private Class<?> eventType;

    public SubscribeMethod(Method method, ThreadMode threadMode, Class<?> eventType) {
        this.method = method;
        this.threadMode = threadMode;
        this.eventType = eventType;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public ThreadMode getThreadMode() {
        return threadMode;
    }

    public void setThreadMode(ThreadMode threadMode) {
        this.threadMode = threadMode;
    }

    public Class<?> getEventType() {
        return eventType;
    }

    public void setEventType(Class<?> eventType) {
        this.eventType = eventType;
    }
}