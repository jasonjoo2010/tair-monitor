package com.taobao.common.tair.packet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.taobao.common.tair.comm.Transcoder;
import com.taobao.common.tair.etc.TairConstant;
import com.taobao.common.tair.etc.TairUtil;

public class ResponseGetGroupPacket extends BasePacket {
    private int                             configVersion;
    private List<Long> serverList;
    private Map<String, String> configMap;

/**
     * ���캯��
     * 
     * @param transcoder
     */
    public ResponseGetGroupPacket(Transcoder transcoder) {
        super(transcoder);
        this.pcode = TairConstant.TAIR_RESP_GET_GROUP_NEW_PACKET;
    }

    /**
     * encode
     */
    public int encode() {
        assert false : "ResponseGetGroupPacket encode unsupport.";
        return 0;
    }

 
    /**
     * decode
     */
    public boolean decode() {
        this.serverList    = new ArrayList<Long>();
        this.configMap = new HashMap<String, String>();
        
        this.configVersion = byteBuffer.getInt();

        // get config items
        int count = byteBuffer.getInt();
        for(int i=0; i<count; i++) {
        	String name = readString();
        	String value = readString();
        	configMap.put(name, value);
        }
        
        // get server list
        count = byteBuffer.getInt();
        if (count > 0) {
        	byte[] b = new byte[count];
        	byteBuffer.get(b);
        	byte[] result = TairUtil.deflate(b);
        	ByteBuffer buff = ByteBuffer.wrap(result);
        	buff.order(ByteOrder.LITTLE_ENDIAN);
        	
        	List<Long> ss = new ArrayList<Long>(TairConstant.TAIR_SERVER_BUCKET_COUNT);
        	boolean valid = false;
        	int c = 0;
        	while (buff.hasRemaining()) {
				long sid = buff.getLong();
				if (!valid) {
					valid = sid != 0; // ������ID��Ϊ0����ID����
				}
				ss.add(sid);
				c++;
				if (c == TairConstant.TAIR_SERVER_BUCKET_COUNT) {
					if (valid) {
						serverList.addAll(ss);
						ss = new ArrayList<Long>(TairConstant.TAIR_SERVER_BUCKET_COUNT);
					}
					c = 0;
					valid = false;
				}
			}
        }
        
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
     * ���������б��б��index��Ϊhashֵ��ֵΪip:port
     * @return
     */
	public List<Long> getServerList() {
		return serverList;
	}

	public Map<String, String> getConfigMap() {
		return configMap;
	}
    
}
