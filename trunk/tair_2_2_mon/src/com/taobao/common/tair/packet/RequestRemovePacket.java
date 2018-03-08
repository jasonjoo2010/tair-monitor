package com.taobao.common.tair.packet;

import com.taobao.common.tair.comm.Transcoder;
import com.taobao.common.tair.etc.TairConstant;

public class RequestRemovePacket extends RequestGetPacket {
/**
     * ¹¹Ôìº¯Êý
     * 
     * @param transcoder
     */
    public RequestRemovePacket(Transcoder transcoder) {
        super(transcoder);
        this.pcode = TairConstant.TAIR_REQ_REMOVE_PACKET;
    }
}
