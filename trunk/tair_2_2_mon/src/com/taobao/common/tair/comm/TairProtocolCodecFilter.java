/**
 * High-Speed Service Framework (HSF)
 * 
 * www.taobao.com
 * 	(C) �Ա�(�й�) 2003-2008
 */
package com.taobao.common.tair.comm;

import org.apache.mina.filter.codec.ProtocolCodecFilter;

import com.taobao.common.tair.packet.PacketStreamer;

/**
 * ������Tair���л�/�����л��Ĵ���
 *
 * @author <a href="mailto:bixuan@taobao.com">bixuan</a>
 */
public class TairProtocolCodecFilter extends ProtocolCodecFilter {

	public TairProtocolCodecFilter(PacketStreamer pstreamer) {
		super(new TairProtocolEncoder(), new TairProtocolDecoder(pstreamer));
	}

}
