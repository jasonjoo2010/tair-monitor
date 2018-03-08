package com.taobao.common.tair.packet;

import com.taobao.common.tair.comm.Transcoder;
import com.taobao.common.tair.etc.TairConstant;

public class ResponseIncDecPacket extends BasePacket {
    private int configVersion = 0;
    private int value         = 0;

/**
     * 构造函数
     * 
     * @param transcoder
     */
    public ResponseIncDecPacket(Transcoder transcoder) {
        super(transcoder);
        this.pcode = TairConstant.TAIR_RESP_INCDEC_PACKET;
    }

    /**
     * encode
     */
    public int encode() {
        // 分配一ByteBuffer, 并写packetHeader
        writePacketBegin(0);

        // body
        byteBuffer.putInt(this.configVersion);
        byteBuffer.putInt(this.value);

        // 结束, 计算出长度
        writePacketEnd();

        return 0;
    }

    /**
     * decode
     */
    public boolean decode() {
        this.configVersion = byteBuffer.getInt();
        this.value         = byteBuffer.getInt();
        return true;
    }

    /**
     * 
     * @return the value
     */
    public int getValue() {
        return value;
    }

    /**
     * 
     * @param value the value to set
     */
    public void setValue(int value) {
        this.value = value;
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
}
