package com.taobao.common.tair.packet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.taobao.common.tair.comm.Transcoder;
import com.taobao.common.tair.etc.TairConstant;

public class RequestGetPacket extends BasePacket {
    protected short       namespace;
    protected Set<Object> keyList = new TreeSet<Object>();

/**
     * ���캯��
     * 
     * @param transcoder
     */
    public RequestGetPacket(Transcoder transcoder) {
        super(transcoder);
        this.pcode = TairConstant.TAIR_REQ_GET_PACKET;
    }

    /**
     * encode
     */
    public int encode() {
        int          capacity = 0;
        List<byte[]> list     = new ArrayList<byte[]>();

        for (Object key : keyList) {
            byte[] keyByte = transcoder.encode(key);
            
            if(keyByte.length > TairConstant.TAIR_KEY_MAX_LENTH) {
            	return 1;
            }

            list.add(keyByte);
            capacity += 4;
            capacity += keyByte.length;
        }

        // ����һByteBuffer, ��дpacketHeader
        writePacketBegin(capacity);

        // body
        byteBuffer.put((byte)0);
        byteBuffer.putShort(namespace);
        byteBuffer.putInt(list.size());
        
        for (byte[] keyByte : list) {
            byteBuffer.putShort((short) keyByte.length);
            byteBuffer.put(keyByte);
        }

        // ����, ���������
        writePacketEnd();

        return 0;
    }

    /**
     * decode
     */
    public boolean decode() {
    	byteBuffer.get(); // ȥ��ͷ���������ʶ
        this.namespace = byteBuffer.getShort();

        int    count = byteBuffer.getInt();
        byte[] dst   = null;

        for (int i = 0; i < count; i++) {
            int keySize = byteBuffer.getShort();

            if (keySize > 0) {
                dst = new byte[keySize];
                byteBuffer.get(dst);
                this.keyList.add(transcoder.decode(dst));
            }
        }

        return true;
    }

    /**
     * ����һ��Key
     *
     * @param key
     * @return �������ɹ�������true���������ʧ�ܣ�ͨ����ԭʼ�Ѿ����ڣ�����false
     */
    public boolean addKey(Object key) {
        return this.keyList.add(key);
    }

    /**
     * 
     * @return the keyList
     */
    public Set<Object> getKeyList() {
        return keyList;
    }

    /**
     * 
     * @param keyList the keyList to set
     */
    public void setKeyList(Set<Object> keyList) {
        this.keyList = keyList;
    }

    /**
     * 
     * @return the namespace
     */
    public short getNamespace() {
        return namespace;
    }

    /**
     * 
     * @param namespace the namespace to set
     */
    public void setNamespace(short namespace) {
        this.namespace = namespace;
    }
}
