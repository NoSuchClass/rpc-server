package com.bitongchong;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author liuyuehe
 * @date 2020/3/25 21:17
 */
@Component
public class RpcServiceServer implements ApplicationContextAware, InitializingBean {
    Map<String, Object> handleMap = new HashMap<>(16);

    @Override
    public void afterPropertiesSet() throws Exception {
        ExecutorService executorService = Executors.newCachedThreadPool();
        int port = getPublishPort();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                executorService.execute(new ProcessorHandler(socket, handleMap));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // 相当于就是将 实现类名称：具体实现类  --> 对外暴露的接口名称：具体实现类
        Map<String, Object> beansMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (!beansMap.isEmpty()) {
            for (Object bean : beansMap.values()) {
                RpcService annotation = bean.getClass().getAnnotation(RpcService.class);
                String serviceName = annotation.value().getName();
                String version = annotation.version();
                String name = serviceName + "-" + version;
                // 这儿客户端只要对传过来的接口名添加上版本号就能够版本控制了
                handleMap.put(name, bean);
            }
        }
    }

    private int getPublishPort() {
        return 8080;
    }
}
