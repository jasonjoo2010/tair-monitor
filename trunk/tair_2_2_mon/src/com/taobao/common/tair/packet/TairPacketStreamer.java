package com.taobao.common.tair.packet;

import java.nio.ByteBuffer;

import com.taobao.common.tair.comm.Transcoder;
import com.taobao.common.tair.etc.TairConstant;

public class TairPacketStreamer implements PacketStreamer {
    private Transcoder transcoder = null;

/**
     * ¹¹Ôìº¯Êý
     * @param transcoder
     */
    public TairPacketStreamer(Transcoder transcoder) {
        this.transcoder = transcoder;
    }

	public BasePacket decodePacket(int pcode, byte[] data) {
		BasePacket packet = createPacket(pcode);
		
		if (packet != null) {
			packet.setLen(data.length);
			packet.setByteBuffer(ByteBuffer.wrap(data));
			packet.decode();
		}
		return packet;
	}

    private BasePacket createPacket(int pcode) {
        BasePacket packet = null;

        switch (pcode) {
            case TairConstant.TAIR_RESP_RETURN_PACKET:
                packet = new ReturnPacket(transcoder);
                break;

            case TairConstant.TAIR_RESP_GET_PACKET:
                packet = new ResponseGetPacket(transcoder);
                break;

            case TairConstant.TAIR_RESP_INCDEC_PACKET:
                packet = new ResponseIncDecPacket(transcoder);
                break;
                
            case TairConstant.TAIR_RESP_GET_GROUP_NEW_PACKET:
            	packet = new ResponseGetGroupPacket(null);
            	break;
            
            case TairConstant.TAIR_RESP_STAT_PACKET:
            	packet = new ResponseStatPacket(null);
            	break;
            	
            default:
        }

        if ((packet != null) && (packet.getPcode() != pcode)) {
            packet = null;
        }

        return packet;
    }


}
