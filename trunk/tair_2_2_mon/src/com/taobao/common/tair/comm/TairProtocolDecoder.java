/**
 * High-Speed Service Framework (HSF)
 * 
 * www.taobao.com
 * 	(C) 淘宝(中国) 2003-2008
 */
package com.taobao.common.tair.comm;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.taobao.common.tair.etc.TairConstant;
import com.taobao.common.tair.packet.BasePacket;
import com.taobao.common.tair.packet.PacketStreamer;

/**
 * 描述：Tair接收响应时的反序列化处理
 *
 * @author <a href="mailto:bixuan@taobao.com">bixuan</a>
 */
public class TairProtocolDecoder extends CumulativeProtocolDecoder {

	private static final Log LOGGER = LogFactory.getLog(TairProtocolDecoder.class);
	
	private static final boolean isDebugEnabled=LOGGER.isDebugEnabled();
	
	private static final int PROTOCOL_HEADER_LENGTH = 16;
	
	private PacketStreamer pstreamer;
	
	public TairProtocolDecoder(PacketStreamer pstreamer) {
		this.pstreamer = pstreamer;
		
	}
	
	/* (non-Javadoc)
	 * @see org.apache.mina.filter.codec.CumulativeProtocolDecoder#doDecode(org.apache.mina.common.IoSession, org.apache.mina.common.ByteBuffer, org.apache.mina.filter.codec.ProtocolDecoderOutput)
	 */
	@Override
	protected boolean doDecode(IoSession session, ByteBuffer in, ProtocolDecoderOutput out) 
		throws Exception {
		final int origonPos = in.position();
        final int packetLength = in.remaining();
        
        if(packetLength<PROTOCOL_HEADER_LENGTH){
        	in.position(origonPos);
        	return false;
        }
        
        int flag  = in.getInt();
        int chid  = in.getInt();
        int pcode = in.getInt();
        int len   = in.getInt();
        
        if (flag != TairConstant.TAIR_PACKET_FLAG)
        	throw new IOException("flag error, except: " + TairConstant.TAIR_PACKET_FLAG + ", but get " + flag);
        
        if (in.remaining() < len) {
        	in.position(origonPos);
        	return false;
        }
        
        if(isDebugEnabled){
	        final String remoteIP = ((InetSocketAddress) session.getRemoteAddress()).getAddress().getHostAddress();
	        StringBuilder receiveTimeInfo = new StringBuilder();
	        receiveTimeInfo.append("receive response from [").append(remoteIP).append("],time is: ");
	        receiveTimeInfo.append(System.currentTimeMillis());
	        receiveTimeInfo.append(", channel id: ").append(chid);
	        LOGGER.debug(receiveTimeInfo.toString());
	        
	        LOGGER.debug("pcode is " + pcode);
        }
        
        byte[] data = new byte[len];
        in.get(data);
        BasePacket returnPacket = pstreamer.decodePacket(pcode, data);
        
        TairResponse response=new TairResponse();
        response.setRequestId(chid);
        response.setResponse(returnPacket);
        out.write(response);
        
		return true;
	}

}
