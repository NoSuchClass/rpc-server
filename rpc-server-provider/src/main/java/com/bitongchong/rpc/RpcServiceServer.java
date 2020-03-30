package com.bitongchong.rpc;

import com.bitongchong.util.ThreadPoolsUtil;
import com.bitongchong.zk.IRegisterCenter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liuyuehe
 * @date 2020/3/25 21:17
 */

public class RpcServiceServer {
    private IRegisterCenter registerCenter;
    /**
     * 格式： ip ： port
     */
    private String serviceAddress;

    public RpcServiceServer(IRegisterCenter registerCenter, String serviceAddress) {
        this.registerCenter = registerCenter;
        this.serviceAddress = serviceAddress;
    }

    Map<String, Object> handleMap = new HashMap<>(16);

    public void start() {
        int port = getPublishPort();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                ThreadPoolsUtil.getInstance().submitTask(new ProcessorHandler(socket, handleMap));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void bind(ApplicationContext applicationContext) {
        Map<String, Object> beansMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (!beansMap.isEmpty()) {
            for (Object bean : beansMap.values()) {
                RpcService annotation = bean.getClass().getAnnotation(RpcService.class);
                String serviceName = annotation.value().getName();
                String version = annotation.version();
                String name = serviceName + "-" + version;
                registerCenter.register(name, serviceAddress);
                // 这儿客户端只要对传过来的接口名添加上版本号就能够版本控制了
                handleMap.put(name, bean);
            }
        }
    }

    private int getPublishPort() {
        String[] addressInfo = serviceAddress.split(":");
        return Integer.parseInt(addressInfo[1]);
    }

    private int getPublishIp() {
        String[] addressInfo = serviceAddress.split(":");
        return Integer.parseInt(addressInfo[0]);
    }
}
