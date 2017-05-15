package com.example.he.eventbus;

import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by he on 2017/5/14.
 *
 */

public class EventBus {

    private Map<Object, List<SubscribeMethod>> cacheMap;
    private Handler mHandler;
    private ExecutorService executorService;
    private static EventBus instance;

    private EventBus() {
        cacheMap = new HashMap<>();
        mHandler = new Handler(Looper.getMainLooper());
        executorService = Executors.newCachedThreadPool();
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
                if (parameters.length != 1) {
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

    public void post(final Object obj) {
        Set<Object> set = cacheMap.keySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            final Object activity = iterator.next();
            List<SubscribeMethod> list = cacheMap.get(activity);
            for (final SubscribeMethod method : list) {
                if (method.getEventType().isAssignableFrom(obj.getClass())) {
                    //判断当前接收方法在哪个线程
                    switch (method.getThreadMode()) {
                        case PostThread:
                            invoke(activity, method, obj);
                            break;
                        case MainThread:
                            //判断发送线程是那个线程  发送线程就是在主线程，不需要线程切换
                            if (Looper.myLooper() == Looper.getMainLooper()) {
                                invoke(activity, method, obj);
                            } else {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(activity, method, obj);
                                    }
                                });
                            }
                            break;
                        case BackgroundThread:
                            if (Looper.getMainLooper() != Looper.myLooper()) {
                                invoke(activity, method, obj);
                            } else {
                                executorService.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(activity, method, obj);
                                    }
                                });
                            }
                            break;
                    }

                }
            }
        }
    }

    private void invoke(Object activity, SubscribeMethod method, Object obj) {
        try {
            method.getMethod().invoke(activity, obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void unregister(Object obj) {
        cacheMap.remove(obj);

    }
}
