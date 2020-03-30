package com.bitongchong.zk;

import com.bitongchong.config.ZkInfoConfig;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.stereotype.Component;

/**
 * @author liuyuehe
 * @date 2020/3/30 13:40
 */

public class RegisterCenterImpl implements IRegisterCenter {
    CuratorFramework zkServer;

    public RegisterCenterImpl() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        zkServer = CuratorFrameworkFactory.builder()
                .connectString(ZkInfoConfig.CONNECT_INFO)
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .namespace(ZkInfoConfig.CONNECT_NAMESPACE)
                .build();
        zkServer.start();
    }

    @Override
    public void register(String serviceName, String serviceAddress) {
        String servicePath = "/" + serviceName;
        try {
            if (zkServer.checkExists().forPath(servicePath) == null) {
                zkServer.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(servicePath);
            }
            String addressPath = servicePath + "/" + serviceAddress;
            String node = zkServer.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(addressPath);
            System.out.println("create node success : " + node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
