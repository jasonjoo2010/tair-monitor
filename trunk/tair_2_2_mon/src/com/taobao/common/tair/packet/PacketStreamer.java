/**
 * 
 */
package com.taobao.common.tair.packet;


/**
 * @author ruohai
 *
 */
public interface PacketStreamer {

	BasePacket decodePacket(int pcode, byte[] data);
}
