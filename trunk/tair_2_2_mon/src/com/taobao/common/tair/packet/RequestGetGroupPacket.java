package com.taobao.common.tair.packet;

import com.taobao.common.tair.comm.Transcoder;
import com.taobao.common.tair.etc.TairConstant;

public class RequestGetGroupPacket extends BasePacket {
    private String groupName;
    private int    configVersion;

/**
     * 构造函数
     * 
     * @param transcoder
     */
    public RequestGetGroupPacket(Transcoder transcoder) {
        super(transcoder);
        this.pcode = TairConstant.TAIR_REQ_GET_GROUP_NEW_PACKET;
    }

    /**
     * encode
     */
    public int encode() {
        // 分配一ByteBuffer, 并写packetHeader
        writePacketBegin(0);

        // body
        byteBuffer.putInt(this.configVersion);
        writeString(this.groupName);

        // 结束, 计算出长度
        writePacketEnd();

        return 0;
    }

    /**
     * decode
     */
    public boolean decode() {
        this.configVersion = byteBuffer.getInt();
        this.groupName     = readString();
        return true;
    }

    /**
     * 
     * @return the configVersion
     */
    public int getConfigVersion() {
        return configVersion;
    }

    /**
     * 
     * @param configVersion the configVersion to set
     */
    public void setConfigVersion(int configVersion) {
        this.configVersion = configVersion;
    }

    /**
     * 
     * @return the groupName
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * 
     * @param groupName the groupName to set
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
