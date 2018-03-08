package com.taobao.common.tair.packet;

import com.taobao.common.tair.comm.Transcoder;
import com.taobao.common.tair.etc.TairConstant;

public class RequestIncDecPacket extends BasePacket {
    private short  namespace      = 0;
    private int    count     = 1;
    private int    initValue = 0;
    private Object key       = null;

/**
     * 构造函数
     * 
     * @param transcoder
     */
    public RequestIncDecPacket(Transcoder transcoder) {
        super(transcoder);
        this.pcode = TairConstant.TAIR_REQ_INCDEC_PACKET;
    }

    /**
     * encode
     */
    public int encode() {
        byte[] keyByte = transcoder.encode(this.key);
        
        if(keyByte.length > TairConstant.TAIR_KEY_MAX_LENTH) {
        	return 1;
        }
        
        // 分配一ByteBuffer, 并写packetHeader
        writePacketBegin(keyByte.length);

        // body
        byteBuffer.put((byte)0);
        byteBuffer.putShort(namespace);
        byteBuffer.putInt(count);
        byteBuffer.putInt(initValue);
        byteBuffer.putShort((short) keyByte.length);
        byteBuffer.put(keyByte);

        // 结束, 计算出长度
        writePacketEnd();

        return 0;
    }

    /**
     * decode
     */
    public boolean decode() {
    	byteBuffer.get(); // 去掉头部的请求标识
        this.namespace      = byteBuffer.getShort();
        this.count     = byteBuffer.getInt();
        this.initValue = byteBuffer.getInt();

        int keySize    = byteBuffer.getShort();

        if (keySize > 0) {
            byte[] dst = new byte[keySize];

            this.key = transcoder.decode(dst);
        }

        return true;
    }

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * @return the initValue
	 */
	public int getInitValue() {
		return initValue;
	}

	/**
	 * @param initValue the initValue to set
	 */
	public void setInitValue(int initValue) {
		this.initValue = initValue;
	}

	/**
	 * @return the key
	 */
	public Object getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(Object key) {
		this.key = key;
	}

	/**
	 * @return the namespace
	 */
	public short getNamespace() {
		return namespace;
	}

	/**
	 * @param namespace the namespace to set
	 */
	public void setNamespace(short namespace) {
		this.namespace = namespace;
	}

}
