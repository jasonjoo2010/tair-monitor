package com.taobao.common.tair.packet;

import com.taobao.common.tair.comm.Transcoder;
import com.taobao.common.tair.etc.TairConstant;

public class RequestPingPacket extends BasePacket {

    private int    configVersion;
    private int    value;
    

/**
     * ���캯��
     * 
     * @param transcoder
     */
    public RequestPingPacket(Transcoder transcoder) {
        super(transcoder);
        this.pcode = TairConstant.TAIR_REQ_PING_PACKET;
        configVersion = 0;
        value =0;
    }

    /**
     * encode
     */
    public int encode() {

        // ����һByteBuffer, ��дpacketHeader
        writePacketBegin(0);

        // body
        byteBuffer.putInt(configVersion);
        byteBuffer.putInt(value);

        // ����, ���������
        writePacketEnd();

        return 0;
    }

    /**
     * decode
     */
    public boolean decode() {
    	this.configVersion = byteBuffer.getInt();
    	this.value = byteBuffer.getInt();
    	
        return true;
    }

	public int getConfigVersion() {
		return configVersion;
	}

	public void setConfigVersion(int configVersion) {
		this.configVersion = configVersion;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}


    

}
