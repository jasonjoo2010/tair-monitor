package com.taobao.common.tair.packet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestCommandCollection {
    private Map<Long, BasePacket> requestCommandMap = new HashMap<Long, BasePacket>();
    private List<BasePacket>      resultList        = new ArrayList<BasePacket>();

/**
     * 构造函数
     */
    public RequestCommandCollection() {
    }

    public BasePacket findRequest(long addr) {
        return requestCommandMap.get(addr);
    }

    /**
     * 加入一请求
     *
     * @param addr
     * @param packet
     */
    public void addRequest(long addr, BasePacket packet) {
        requestCommandMap.put(addr, packet);
    }

    /**
     * 
     * @return the requestCommandMap
     */
    public Map<Long, BasePacket> getRequestCommandMap() {
        return requestCommandMap;
    }

    /**
     * 
     * @param requestCommandMap the requestCommandMap to set
     */
    public void setRequestCommandMap(Map<Long, BasePacket> requestCommandMap) {
        this.requestCommandMap = requestCommandMap;
    }

    /**
     * 
     * @return the resultList
     */
    public List<BasePacket> getResultList() {
        return resultList;
    }

    /**
     * 
     * @param resultList the resultList to set
     */
    public void setResultList(List<BasePacket> resultList) {
        this.resultList = resultList;
    }
}
