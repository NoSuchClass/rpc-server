package com.bitongchong.rpc;

import lombok.Data;

import java.io.Serializable;

/**
 * @author liuyuehe
 * @date 2020/3/25 11:02
 */
@Data
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = 620739987228434511L;
    private String className;
    private String methodName;
    private Object[] params;
    private String version;
}
