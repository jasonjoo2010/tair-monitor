package com.taobao.common.tair.packet;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.taobao.common.tair.CTDBMStat;
import com.taobao.common.tair.DataEntry;
import com.taobao.common.tair.comm.Transcoder;
import com.taobao.common.tair.etc.TairConstant;

public class ResponseStatPacket extends BasePacket {
    private int             configVersion;
    private int            	statType;
    private long           	serverId;
    private byte[] 			buffer;
    

/**
     * 构造函数
     * 
     * @param transcoder
     */
    public ResponseStatPacket(Transcoder transcoder) {
        super(transcoder);
        this.pcode = TairConstant.TAIR_RESP_STAT_PACKET;
        buffer = null;
    }

    /**
     * encode
     */
    public int encode() {
        // 分配一ByteBuffer, 并写packetHeader
        writePacketBegin(buffer.length+16);

        byteBuffer.putInt(configVersion);
        byteBuffer.putLong(serverId);
        byteBuffer.putInt(statType);
        byteBuffer.putInt(buffer.length);
        if(buffer.length>0)
        	byteBuffer.put(buffer);

        // 结束, 计算出长度
        writePacketEnd();

        return 0;
    }

    /**
     * decode
     */
    public boolean decode() {
        this.configVersion = byteBuffer.getInt();
        this.serverId = byteBuffer.getLong();
        this.statType = byteBuffer.getInt();
        int length = byteBuffer.getInt();
        this.buffer = new byte[length];
        for(int i=0;i<length;i++)
        	buffer[i]=byteBuffer.get();
        return true;
    }

	public int getConfigVersion() {
		return configVersion;
	}

	public void setConfigVersion(int configVersion) {
		this.configVersion = configVersion;
	}

	public int getStatType() {
		return statType;
	}

	public void setStatType(int statType) {
		this.statType = statType;
	}

	public long getServerId() {
		return serverId;
	}

	public void setServerId(long serverId) {
		this.serverId = serverId;
	}

	public byte[] getBuffer() {
		return buffer;
	}

	public void setBuffer(byte[] buffer) {
		this.buffer = buffer;
	}
    
	public static long byteToLong(byte[] b) {
		long s = 0;
		long s0 = b[0] & 0xff;// 最低位
		long s1 = b[1] & 0xff;
		long s2 = b[2] & 0xff;
		long s3 = b[3] & 0xff;
		long s4 = b[4] & 0xff;// 最低位
		long s5 = b[5] & 0xff;
		long s6 = b[6] & 0xff;
		long s7 = b[7] & 0xff;
		// s0不变
		s1 <<= 8;
		s2 <<= 16;
		s3 <<= 24;
		s4 <<= 8 * 4;
		s5 <<= 8 * 5;
		s6 <<= 8 * 6;
		s7 <<= 8 * 7;
		s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;
		return s;
	}

	public static int byteToInt(byte[] b) {
		int s = 0;
		int s0 = b[0] & 0xff;// 最低位
		int s1 = b[1] & 0xff;
		int s2 = b[2] & 0xff;
		int s3 = b[3] & 0xff;
		s3 <<= 24;
		s2 <<= 16;
		s1 <<= 8;
		s = s0 | s1 | s2 | s3;
		return s;
	}
	public byte[] getSubBuffer(int begin,int end){
		byte[] data = new byte[end-begin+1];
		int j=0;
		for(int i=begin ;i<=end ;i++)
			data[j++]=buffer[i];
		return data;
	}
	public CTDBMStat getCTDBMStat(){
		CTDBMStat stat = new CTDBMStat();
		stat.setGetCount(byteToLong(getSubBuffer(0,7)));
		stat.setPutCount(byteToLong(getSubBuffer(8,15)));
		stat.setEvictCount(byteToLong(getSubBuffer(16,23)));
		stat.setRemoveCount(byteToLong(getSubBuffer(24,31)));
		stat.setHitCount(byteToLong(getSubBuffer(32,39)));
		stat.setRequestCount(byteToLong(getSubBuffer(40,47)));
		stat.setReadBytes(byteToLong(getSubBuffer(48,55)));
		stat.setWriteBytes(byteToLong(getSubBuffer(56,63)));
		stat.setDataSize(byteToLong(getSubBuffer(64,71)));
		stat.setUseSize(byteToLong(getSubBuffer(72,79)));
		stat.setItemCount(byteToInt(getSubBuffer(80,83)));
		stat.setCurrLoad(byteToInt(getSubBuffer(84,87)));
		stat.setStartupTime(byteToInt(getSubBuffer(88,91)));
		return stat;
	}
	public List<Integer> getArea(){
		int use = byteToInt(getSubBuffer(0,3));
		int max = byteToInt(getSubBuffer(4,7));
		LinkedList<Integer> areaList = new LinkedList<Integer>();
		areaList.clear();
		for(int i=0;i<=use;i++){
			int a=i/8;
			int b=i%8;
			if( ( (buffer[8+a]) & (1<<b) ) != 0 ){
				areaList.add(i);
			}
		}
		return areaList;
	}
}
