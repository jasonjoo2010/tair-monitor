package com.taobao.common.tair.packet;

import java.util.ArrayList;
import java.util.List;

import com.taobao.common.tair.DataEntry;
import com.taobao.common.tair.comm.Transcoder;
import com.taobao.common.tair.etc.TairConstant;

public class ResponseGetPacket extends BasePacket {
    private int             configVersion;
    private List<DataEntry> entryList;

/**
     * 构造函数
     * 
     * @param transcoder
     */
    public ResponseGetPacket(Transcoder transcoder) {
        super(transcoder);
        this.pcode = TairConstant.TAIR_RESP_GET_PACKET;
    }

    /**
     * encode
     */
    public int encode() {
        List<byte[]> list     = new ArrayList<byte[]>();
        int          capacity = 0;

        for (DataEntry de : entryList) {
            byte[] keyByte  = transcoder.encode(de.getKey());
            byte[] dataByte = transcoder.encode(de.getValue());

            capacity += (8 + keyByte.length + dataByte.length);
            list.add(keyByte);
            list.add(dataByte);
        }

        // 分配一ByteBuffer, 并写packetHeader
        writePacketBegin(capacity);

        // body
        int index = 0;

        byteBuffer.putInt(configVersion);
        byteBuffer.putInt(list.size() / 2);

        for (byte[] keyByte : list) {
            if ((index++ % 2) == 0) {
                byteBuffer.putShort((short) keyByte.length);
            } else {
                byteBuffer.putInt(keyByte.length);
            }

            byteBuffer.put(keyByte);
        }

        // 结束, 计算出长度
        writePacketEnd();

        return 0;
    }

    /**
     * decode
     */
    public boolean decode() {
        this.configVersion = byteBuffer.getInt();

        int    count   = byteBuffer.getInt();
        int    size    = 0;
        int    version = 0;
        Object key     = null;
        Object value   = null;

        this.entryList = new ArrayList<DataEntry>(count);

        for (int i = 0; i < count; i++) {
            version = byteBuffer.getShort();

            size = byteBuffer.getShort();

            if (size > 0) {
                key = transcoder.decode(byteBuffer.array(), byteBuffer.position(), size);
                byteBuffer.position(byteBuffer.position() + size);
            }

            size = byteBuffer.getInt();

            if (size > 0) {
                value = transcoder.decode(byteBuffer.array(), byteBuffer.position(), size);
                byteBuffer.position(byteBuffer.position() + size);
            }

            this.entryList.add(new DataEntry(key, value, version));
        }

        return true;
    }

    public List<DataEntry> getEntryList() {
        return entryList;
    }

    public void setEntryList(List<DataEntry> entryList) {
        this.entryList = entryList;
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
