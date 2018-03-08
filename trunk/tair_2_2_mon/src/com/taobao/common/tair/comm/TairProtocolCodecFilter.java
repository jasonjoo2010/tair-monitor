/**
 * High-Speed Service Framework (HSF)
 * 
 * www.taobao.com
 * 	(C) 淘宝(中国) 2003-2008
 */
package com.taobao.common.tair.comm;

import org.apache.mina.filter.codec.ProtocolCodecFilter;

import com.taobao.common.tair.packet.PacketStreamer;

/**
 * 描述：Tair序列化/反序列化的处理
 *
 * @author <a href="mailto:bixuan@taobao.com">bixuan</a>
 */
public class TairProtocolCodecFilter extends ProtocolCodecFilter {

	public TairProtocolCodecFilter(PacketStreamer pstreamer) {
		super(new TairProtocolEncoder(), new TairProtocolDecoder(pstreamer));
	}

}
