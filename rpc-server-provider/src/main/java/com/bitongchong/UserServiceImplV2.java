package com.bitongchong;

import com.bitongchong.rpc.IUserService;

/**
 * @author liuyuehe
 * @date 2020/3/25 10:10
 */
@RpcService(value = IUserService.class, version = "v2.0")
public class UserServiceImplV2 implements IUserService {
    String info = "初始info";
    @Override
    public String getInfo(String info) {
        return "V2.0 - GetInfo RPC Request in, and the info is : " + info;
    }

    @Override
    public String addInfo(String info) {
        return "V2.0 - AddInfo RPC Request in, and the info is : " + this.info + info;
    }
}
