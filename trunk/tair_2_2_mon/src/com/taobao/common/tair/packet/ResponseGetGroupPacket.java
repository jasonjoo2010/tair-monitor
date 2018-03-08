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
     * 构造函数
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
					valid = sid != 0; // 当机器ID不为0，则ID正常
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
     * 服务器的列表，列表的index即为hash值，值为ip:port
     * @return
     */
	public List<Long> getServerList() {
		return serverList;
	}

	public Map<String, String> getConfigMap() {
		return configMap;
	}
    
}
