package com.taobao.common.tair.packet;

import com.taobao.common.tair.comm.Transcoder;
import com.taobao.common.tair.etc.TairConstant;

public class RequestStatPacket extends RequestPingPacket {

	public RequestStatPacket(Transcoder transcoder) {
		super(transcoder);
		this.pcode = TairConstant.TAIR_REQ_STAT_PACKET;
	}
}
