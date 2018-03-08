/**
 * High-Speed Service Framework (HSF)
 * 
 * www.taobao.com
 * 	(C) �Ա�(�й�) 2003-2008
 */
package com.taobao.common.tair.comm;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * ���������Ͷ���ʱ�����л�����
 *
 * @author <a href="mailto:bixuan@taobao.com">bixuan</a>
 */
public class TairProtocolEncoder extends ProtocolEncoderAdapter {

	/* (non-Javadoc)
	 * @see org.apache.mina.filter.codec.ProtocolEncoder#encode(org.apache.mina.common.IoSession, java.lang.Object, org.apache.mina.filter.codec.ProtocolEncoderOutput)
	 */
	public void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception {
		// ����Tair���͵�һ�����ֽ��������԰������·�ʽ����
		if(!(message instanceof byte[])){
			throw new Exception("must send byte[]");
		}
		byte[] payload=(byte[]) message;
		ByteBuffer buf = ByteBuffer.allocate(payload.length, false);
        buf.put(payload);
        buf.flip();
        out.write(buf);
	}

}
