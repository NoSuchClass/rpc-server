package com.bitongchong.rpc;

import com.bitongchong.rpc.RpcRequest;
import lombok.SneakyThrows;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liuyuehe
 * @date 2020/3/25 10:36
 */
public class ProcessorHandler implements Runnable{
    private Socket socket;
    Map<String, Object> handleMap;

    public ProcessorHandler(Socket socket, Map<String, Object> handleMap) {
        this.socket = socket;
        this.handleMap = handleMap;
    }

    @SneakyThrows
    @Override
    public void run() {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
            RpcRequest request = (RpcRequest) objectInputStream.readObject();
            Object result = invoke(request);
            objectOutputStream.writeObject(result);
            objectOutputStream.flush();
        }
    }

    private Object invoke(RpcRequest rpcRequest) throws IllegalAccessException, ClassNotFoundException
            , NoSuchMethodException, InvocationTargetException {
        Object[] args = rpcRequest.getParams();
        String className = rpcRequest.getClassName();
        String version = rpcRequest.getVersion();
        if (className == null) {
            throw new RuntimeException("class not found:" + className);
        }
        String methodName = rpcRequest.getMethodName();
        Class<?>[] types = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            types[i] = args[i].getClass();
        }
        Class<?> clazz = Class.forName(className);
        Method method = clazz.getMethod(methodName, types);
        Object service = handleMap.get(className +"-" + version);
        return method.invoke(service, args);
    }
}
