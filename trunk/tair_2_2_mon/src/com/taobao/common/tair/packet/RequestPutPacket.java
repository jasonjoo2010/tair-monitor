package com.taobao.common.tair.packet;

import com.taobao.common.tair.comm.Transcoder;
import com.taobao.common.tair.etc.TairConstant;

public class RequestPutPacket extends BasePacket {
    private short  namespace;
    private short  version;
    private int    expired;
    private Object key;
    private Object data;

/**
     * 构造函数
     * 
     * @param transcoder
     */
    public RequestPutPacket(Transcoder transcoder) {
        super(transcoder);
        this.pcode = TairConstant.TAIR_REQ_PUT_PACKET;
    }

    /**
     * encode
     */
    public int encode() {
        byte[] keyByte  = transcoder.encode(key);
        byte[] dataByte = transcoder.encode(data);
        
        if(keyByte.length > TairConstant.TAIR_KEY_MAX_LENTH) {
        	return 1;        	
        }
        
        if(dataByte.length > TairConstant.TAIR_VALUE_MAX_LENGTH) {
        	return 2;
        }
        
        // 分配一ByteBuffer, 并写packetHeader
        writePacketBegin(keyByte.length + dataByte.length);

        // body
        byteBuffer.put((byte) 0);
        byteBuffer.putShort(namespace);
        byteBuffer.putShort(version);
        byteBuffer.putInt(expired);
        byteBuffer.putShort((short) keyByte.length);
        byteBuffer.putInt(dataByte.length);
        byteBuffer.put(keyByte);
        byteBuffer.put(dataByte);

        // 结束, 计算出长度
        writePacketEnd();

        return 0;
    }

    /**
     * decode
     */
    public boolean decode() {
    	byteBuffer.get(); // 去掉头部的请求标识
        this.namespace    = byteBuffer.getShort();
        this.version = byteBuffer.getShort();
        this.expired = byteBuffer.getInt();

        int    keySize = byteBuffer.getShort();
        int    dataSize = byteBuffer.getInt();
        byte[] dst   = null;

        if (keySize > 0) {
            dst = new byte[keySize];
            byteBuffer.get(dst);
            this.key = transcoder.decode(dst);
        }

        if (dataSize > 0) {
            dst = new byte[dataSize];
            byteBuffer.get(dst);
            this.data = transcoder.decode(dst);
        }

        return true;
    }

	/**
	 * @return the data
	 */
	public Object getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(Object data) {
		this.data = data;
	}

	/**
	 * @return the expired
	 */
	public int getExpired() {
		return expired;
	}

	/**
	 * @param expired the expired to set
	 */
	public void setExpired(int expired) {
		this.expired = expired;
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

	/**
	 * @return the version
	 */
	public short getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(short version) {
		this.version = version;
	}

}
