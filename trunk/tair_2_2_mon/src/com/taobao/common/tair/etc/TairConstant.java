package com.taobao.common.tair.etc;

public class TairConstant {
    // packet flag
    public static final int TAIR_PACKET_FLAG = 0x6d426454;
    
    // packet code
    // request
    public static final int TAIR_REQ_PUT_PACKET    = 1;
    public static final int TAIR_REQ_GET_PACKET    = 2;
    public static final int TAIR_REQ_REMOVE_PACKET = 3;
    public static final int TAIR_REQ_STAT_PACKET = 6;
    public static final int TAIR_REQ_PING_PACKET = 7;
    public static final int TAIR_REQ_INVALID_PACKET = 12;
    public static final int TAIR_REQ_INCDEC_PACKET = 11;

    // response
    public static final int TAIR_RESP_RETURN_PACKET = 101;
    public static final int TAIR_RESP_GET_PACKET    = 102;
    public static final int TAIR_RESP_STAT_PACKET    = 103;
    public static final int TAIR_RESP_INCDEC_PACKET    = 105;

    // config server
    public static final int TAIR_REQ_GET_GROUP_NEW_PACKET  = 1002;
    public static final int TAIR_RESP_GET_GROUP_NEW_PACKET = 1102;
    public static final int TAIR_SERVER_BUCKET_COUNT = 10243;
    
    // serialize type
    public static final int TAIR_STYPE_INT = 1;
    public static final int TAIR_STYPE_STRING = 2;
    public static final int TAIR_STYPE_BOOL = 3;
    public static final int TAIR_STYPE_LONG = 4;
    public static final int TAIR_STYPE_DATE = 5;
    public static final int TAIR_STYPE_BYTE = 6;
    public static final int TAIR_STYPE_FLOAT = 7;
    public static final int TAIR_STYPE_DOUBLE = 8;
    public static final int TAIR_STYPE_BYTEARRAY = 9;
    public static final int TAIR_STYPE_SERIALIZE = 10;
    public static final int TAIR_STYPE_INCDATA = 11;
        
    
	// ͷ����
    public static final int TAIR_PACKET_HEADER_SIZE = 16;
    // bodylen��header��λ��
    public static final int TAIR_PACKET_HEADER_BLPOS = 12;
    
    // buffer size
    public static final int INOUT_BUFFER_SIZE = 8192;
    public static final int DEFAULT_TIMEOUT = 2000;
    public static final int DEFAULT_WAIT_THREAD = 100;
    
    // etc
    /** Ĭ�ϵ�����ѹ����ֵ */
    public static final int TAIR_DEFAULT_COMPRESSION_THRESHOLD = 8192;
    /** �ַ�����Ĭ�ϱ��� */
    public static final String DEFAULT_CHARSET = "UTF-8";
    
    public static final int TAIR_KEY_MAX_LENTH = 1024; // 1KB
    public static final int TAIR_VALUE_MAX_LENGTH =1024*1024; // 1MB
    
    /** ���������ĳ��� */
    public static final int TAIR_MAX_COUNT = 1024;
    /** �·����ڴ�����ֵ */
    public static final int TAIR_MALLOC_MAX = 1 << 20; // 1MB
    
    /** namespace�����ֵ */
    public static final int NAMESPACE_MAX = 65535;
    
    /** ʧЧ��������key */
    public static final String INVALUD_SERVERLIST_KEY = "invalidate_server";
    /** configserver���ڻ����ı�ʶ **/
    public static final String INSTANCE_LOCATION_NAME = "location_name";
    
    /** ʵ���а����Ļ����б� */
    public static final String INSTANCE_NODE_LIST = "server_node_list";
    
    // query type
	public static final int TDBM_STAT_TOTAL = 1;
	public static final int TDBM_STAT_AREA = 4;
	public static final int TDBM_STAT_GET_MAXAREA = 5;
}
