package com.bitongchong;

import com.bitongchong.config.SpringConfig;
import com.bitongchong.rpc.RpcServiceServer;
import com.bitongchong.zk.IRegisterCenter;
import com.bitongchong.zk.RegisterCenterImpl;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        // 这个地方本来可以不依赖Spring的，但是为了方便注解获取注解值，就使用了
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
        context.start();

        IRegisterCenter registerCenter = new RegisterCenterImpl();
        RpcServiceServer server = new RpcServiceServer(registerCenter, "127.0.0.1:8080");
        server.bind(context);
        server.start();
    }
}
