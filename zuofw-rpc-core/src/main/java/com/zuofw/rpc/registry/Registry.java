package com.zuofw.rpc.registry;

public interface Registry {
    public void register(String serviceName, String serviceAddress);

    public String discover(String serviceName);
}
