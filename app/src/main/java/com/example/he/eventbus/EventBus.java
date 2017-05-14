package com.example.he.eventbus;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by he on 2017/5/14.
 *
 */

public class EventBus {

    private Map<Object, List<SubscribeMethod>> cacheMap;
    private static EventBus instance;

    private EventBus() {

    }

    public static EventBus getDefault() {
        if (instance == null) {
            synchronized (EventBus.class) {
                if (instance == null) {
                    instance = new EventBus();
                }
            }
        }
        return instance;
    }

    public void register(Object activity) {
        List<SubscribeMethod> list = cacheMap.get(activity);
        if (list == null) {
            List<SubscribeMethod> methods = findSubscribeMethod(activity);
            cacheMap.put(activity, methods);
        }
    }

    private List<SubscribeMethod> findSubscribeMethod(Object activity) {
        //线程安全
        List<SubscribeMethod> list = new CopyOnWriteArrayList<>();
        Class<?> clazz = activity.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        //循环查找父类的接收方法,
        while (clazz != null) {
            String name = clazz.getName();
            if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("android.")) {
                break;
            }
            for (Method method: methods) {
                Subscribe subscribe = method.getAnnotation(Subscribe.class);
                if (subscribe == null) {
                    continue;
                }
                //拿到方法的参数数组
                Class<?>[] parameters = method.getParameterTypes();
                if (parameters.length != 0) {
                    throw new RuntimeException("eventbus must be one parameter");
                }
                Class<?> parameter = parameters[0];
                //发生的线程
                ThreadMode threadMode = subscribe.value();
                SubscribeMethod subscribeMethod = new SubscribeMethod(method, threadMode, parameter);
                list.add(subscribeMethod);
            }
            clazz = clazz.getSuperclass();
        }
        return list;
    }

    public void post(Object obj) {
        
    }
}
