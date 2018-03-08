package com.taobao.common.tair.packet;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.taobao.common.tair.comm.Transcoder;
import com.taobao.common.tair.etc.TairConstant;

public class BasePacket {
    protected Exception          exception    = null;
    protected ByteBuffer         byteBuffer   = null;
    protected int                chid         = 0;
    protected int                pcode        = 0;
    protected int                len          = 0;
    private BasePacket           returnPacket = null;
    private static AtomicInteger globalChid   = new AtomicInteger(0);
    protected Transcoder         transcoder   = null;
    private long                 startTime    = 0;
    
    // lock & contition
    private ReentrantLock lock;
    private Condition cond;


    public BasePacket(Transcoder transcoder) {
        this.transcoder = transcoder;
    }

    /**
     * 得到byteBuffer
     *
     * @return
     */
    public ByteBuffer getByteBuffer() {
        if (byteBuffer == null) {
            encode();
        }

        return byteBuffer;
    }

    /**
     * 写一string到bytebuffer中
     *
     * @param str
     */
    protected void writeString(String str) {
        if (str == null) {
            byteBuffer.putInt(0);
        } else {
            byte[] b = str.getBytes();

            byteBuffer.putInt(b.length + 1);
            byteBuffer.put(b);
            byteBuffer.put((byte) 0);
        }
    }

    /**
     * 读入string
     *
     * @return
     */
    protected String readString() {
        int len = byteBuffer.getInt();

        if (len <= 1) {
            return "";
        } else {
            byte[] b = new byte[len];

            byteBuffer.get(b);
            return new String(b, 0, len - 1);
        }
    }

    public int encode() {
        return 0;
    }

    public boolean decode() {
        if ((byteBuffer == null) || (byteBuffer.remaining() < len)) {
            return false;
        }

        // 把数据读完
        byte[] tmp = new byte[len];

        byteBuffer.get(tmp);
        return true;
    }

    /**
     * 写packet开始调用  分配ByteBuffer, 并写出Packet头信息
     */
    protected void writePacketBegin(int capacity) {
        // 产生channel id
        chid = globalChid.incrementAndGet();

        // packet header
        byteBuffer = ByteBuffer.allocate(capacity + 256);
        byteBuffer.putInt(TairConstant.TAIR_PACKET_FLAG); // packet flag
        byteBuffer.putInt(chid); // channel id
        byteBuffer.putInt(pcode); // packet code
        byteBuffer.putInt(0); // body len
    }

    /**
     * 写packet结束调用  把bodyLen长度写到packetHeader的位置上
     */
    protected void writePacketEnd() {
        int len = byteBuffer.position() - TairConstant.TAIR_PACKET_HEADER_SIZE;

        byteBuffer.putInt(TairConstant.TAIR_PACKET_HEADER_BLPOS, len);
    }

    public BasePacket getReturnPacket(int timeout) {
    	if(lock == null)
    		return returnPacket;
    	
    	lock.lock();
		try {
			while (returnPacket == null)
				cond.await(timeout, TimeUnit.MILLISECONDS);
		} catch (Exception e) {

		} finally {
			lock.unlock();
		}
		return returnPacket;
    }

    public void setReturnPacket(BasePacket returnPacket) {
    	if (lock == null) {
			this.returnPacket = returnPacket;
			return;
		}
		lock.lock();
		this.returnPacket = returnPacket;
		try {
			cond.signal();
		} finally {
			lock.unlock();
		}
	}

    public void setByteBuffer(ByteBuffer byteBuffer) {
        this.byteBuffer = ByteBuffer.allocate(len);
        byteBuffer.get(this.byteBuffer.array());
    }

    public int getPcode() {
        return pcode;
    }

    public void setPcode(int pcode) {
        this.pcode = pcode;
    }

    public void setChid(int chid) {
        this.chid = chid;
    }

    public int getChid() {
    	if (chid == 0)
    		encode();
        return chid;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return "basepacket: chid=" + chid + ", pcode=" + pcode + ", len=" + len;
    }

    /**
     * 
     * @return the exception
     */
    public Exception getException() {
        return exception;
    }
    
    public void initCondition() {
    	lock = new ReentrantLock();
    	cond = lock.newCondition();
    }

    /**
     * 
     * @param exception the exception to set
     */
    public void setException(Exception exception) {
        this.exception = exception;
    }
}
