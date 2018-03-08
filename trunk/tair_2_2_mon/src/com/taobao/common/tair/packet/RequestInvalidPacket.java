/**
 * 
 */
package com.taobao.common.tair.packet;

import java.util.ArrayList;
import java.util.List;

import com.taobao.common.tair.comm.Transcoder;
import com.taobao.common.tair.etc.TairConstant;

/**
 * 失效的request
 * 
 * @author ruohai
 * 
 */
public class RequestInvalidPacket extends RequestGetPacket {

	private String groupName;

	public RequestInvalidPacket(Transcoder transcoder, String groupName) {
		super(transcoder);
		this.pcode = TairConstant.TAIR_REQ_INVALID_PACKET;
		this.groupName = groupName;
	}

	@Override
	public int encode() {
		int capacity = 0;
		List<byte[]> list = new ArrayList<byte[]>();

		for (Object key : keyList) {
			byte[] keyByte = transcoder.encode(key);

			if (keyByte.length > TairConstant.TAIR_KEY_MAX_LENTH) {
				return 1;
			}

			list.add(keyByte);
			capacity += 4;
			capacity += keyByte.length;
		}

		// 分配一ByteBuffer, 并写packetHeader
		writePacketBegin(capacity);

		// body
		byteBuffer.put((byte) 0);
		byteBuffer.putShort(namespace);
		byteBuffer.putInt(list.size());

		for (byte[] keyByte : list) {
			byteBuffer.putShort((short) keyByte.length);
			byteBuffer.put(keyByte);
		}

		writeString(getGroupName());

		// 结束, 计算出长度
		writePacketEnd();

		return 0;
	}

	@Override
	public boolean decode() {
		byteBuffer.get(); // 去掉头部的请求标识
		this.namespace = byteBuffer.getShort();

		int count = byteBuffer.getInt();
		byte[] dst = null;

		for (int i = 0; i < count; i++) {
			int keySize = byteBuffer.getShort();

			if (keySize > 0) {
				dst = new byte[keySize];
				byteBuffer.get(dst);
				this.keyList.add(transcoder.decode(dst));
			}
		}
		setGroupName(readString());

		return true;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

}
