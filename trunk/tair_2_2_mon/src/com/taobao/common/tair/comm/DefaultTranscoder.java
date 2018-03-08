/**
 * 
 */
package com.taobao.common.tair.comm;

import java.util.Date;

import com.taobao.common.tair.DataEntry;
import com.taobao.common.tair.etc.TairConstant;
import com.taobao.common.tair.etc.TairUtil;
import com.taobao.common.tair.etc.TranscoderUtil;

/**
 * 
 * @author ruohai
 */
public class DefaultTranscoder implements Transcoder {
    private int    compressionThreshold = TairConstant.TAIR_DEFAULT_COMPRESSION_THRESHOLD;
    private String charset              = TairConstant.DEFAULT_CHARSET;

/**
     * 创建一个Transcoder，使用默认的压缩阀值（8192）和字符集（UTF-8）
     */
    public DefaultTranscoder() {
    }

/**
     * 创建一个Transcoder
     * 
     * @param compressionThreshold
     *            启用压缩的阀值
     * @param charset
     *            字符串使用的字符集
     */
    public DefaultTranscoder(int compressionThreshold, String charset) {
        if (compressionThreshold > 0) {
            this.compressionThreshold = compressionThreshold;
        }

        if (charset != null) {
            this.charset = charset;
        }
    }

    public byte[] encode(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("key,value不能为null");
        }

        byte[] b    = null;
        short  flag = 0;

        if (object instanceof String) {
            b    = TranscoderUtil.encodeString((String) object, charset);
            flag = TairConstant.TAIR_STYPE_STRING;
        } else if (object instanceof Long) {
            b    = TranscoderUtil.encodeLong((Long) object);
            flag = TairConstant.TAIR_STYPE_LONG;
        } else if (object instanceof Integer) {
            b    = TranscoderUtil.encodeInt((Integer) object);
            flag = TairConstant.TAIR_STYPE_INT;
        } else if (object instanceof Boolean) {
            b    = TranscoderUtil.encodeBoolean((Boolean) object);
            flag = TairConstant.TAIR_STYPE_BOOL;
        } else if (object instanceof Date) {
            b    = TranscoderUtil.encodeLong(((Date) object).getTime());
            flag = TairConstant.TAIR_STYPE_DATE;
        } else if (object instanceof Byte) {
            b    = TranscoderUtil.encodeByte((Byte) object);
            flag = TairConstant.TAIR_STYPE_BYTE;
        } else if (object instanceof Float) {
            b    = TranscoderUtil.encodeInt(Float.floatToRawIntBits((Float) object));
            flag = TairConstant.TAIR_STYPE_FLOAT;
        } else if (object instanceof Double) {
            b    = TranscoderUtil.encodeLong(Double.doubleToRawLongBits((Double) object));
            flag = TairConstant.TAIR_STYPE_DOUBLE;
        } else if (object instanceof byte[]) {
            b    = (byte[]) object;
            flag = TairConstant.TAIR_STYPE_BYTEARRAY;
        } else {
            b    = TranscoderUtil.serialize(object);
            flag = TairConstant.TAIR_STYPE_SERIALIZE;
        }

        flag <<= 1;

        if (b.length > compressionThreshold) {
            b = TranscoderUtil.compress(b);
            flag += 1;
        }

        TairUtil.checkMalloc(b.length + 2);

        byte[] result = new byte[b.length + 2];
        byte[] fg     = new byte[2];

        fg[1]         = (byte) (flag & 0xFF);
        fg[0]         = (byte) ((flag >> 8) & 0xFF);

        for (int i = 0; i < 2; i++) {
            result[i] = fg[i];
        }

        for (int i = 0; i < b.length; i++) {
            result[i + 2] = b[i];
        }

        System.out.println("dd");
        return result;
    }

    public Object decode(byte[] data) {
        return decode(data, 0, data.length);
    }

    public Object decode(byte[] data, int offset, int size) {
        TairUtil.checkMalloc(size - 2);

        byte[] vb = new byte[size - 2];

        System.arraycopy(data, offset + 2, vb, 0, size - 2);

        Object obj = null;

        int    flags = 0;

        for (int i = 0; i < 2; i++) {
            byte b = data[offset + i];

            flags = (flags << 8) | ((b < 0) ? (256 + b)
                                            : b);
        }

        if ((flags & 1) == 1) {
            vb = TranscoderUtil.decompress(vb);
        }

        int type = (flags >> 1) & 0xF;

        switch (type) {
            case TairConstant.TAIR_STYPE_INT:
                obj = TranscoderUtil.decodeInt(vb);
                break;

            case TairConstant.TAIR_STYPE_STRING:
                obj = TranscoderUtil.decodeString(vb, charset);
                break;

            case TairConstant.TAIR_STYPE_BOOL:
                obj = TranscoderUtil.decodeBoolean(vb);
                break;

            case TairConstant.TAIR_STYPE_LONG:
                obj = TranscoderUtil.decodeLong(vb);
                break;

            case TairConstant.TAIR_STYPE_DATE:

                Long time = TranscoderUtil.decodeLong(vb);

                obj = new Date(time);
                break;

            case TairConstant.TAIR_STYPE_BYTE:
                obj = TranscoderUtil.decodeByte(vb);
                break;

            case TairConstant.TAIR_STYPE_FLOAT:

                Integer f = TranscoderUtil.decodeInt(vb);

                obj = new Float(Float.intBitsToFloat(f));
                break;

            case TairConstant.TAIR_STYPE_DOUBLE:

                Long l = TranscoderUtil.decodeLong(vb);

                obj = new Double(Double.longBitsToDouble(l));
                break;

            case TairConstant.TAIR_STYPE_BYTEARRAY:
                obj = vb;
                break;

            case TairConstant.TAIR_STYPE_SERIALIZE:
                obj = TranscoderUtil.deserialize(vb);
                break;

            case TairConstant.TAIR_STYPE_INCDATA:

                int rv   = 0;
                int bits = 0;

                for (byte i : vb) {
                    rv |= (((i < 0) ? (256 + i)
                                    : i) << bits);
                    bits += 8;
                }

                obj = rv;
                break;

            default:
                throw new RuntimeException("unknow serialize flag: " + type);
        }

        return obj;
    }

    public static void main(String[] args) {
        Transcoder tc = new DefaultTranscoder();

        int        number = Integer.MAX_VALUE;
        int        newnum = (Integer) tc.decode(tc.encode(number));

        if (newnum != number) {
            System.err.println("test int faild, expect " + number + ", got " + newnum);
        }

        long l    = System.currentTimeMillis();
        long newl = (Long) tc.decode(tc.encode(l));

        if (newl != l) {
            System.err.println("test long faild, expect " + l + ", got " + newl);
        }

        String str    = "this is a test string";
        String newstr = (String) tc.decode(tc.encode(str));

        if (!newstr.equals(str)) {
            System.err.println("test string faild, expect [" + str + "], got [" + newstr + "]");
        }

        DataEntry de = new DataEntry(str, str);

        de.setVersion(number);

        DataEntry newde = (DataEntry) tc.decode(tc.encode(de));

        if ((de == null)
                    || ((!de.getValue().toString().equals(str)) && (de.getVersion() != number))) {
            System.err.println("test object failed, got: [value=" + newde.getValue() + ", version="
                               + newde.getVersion() + "]");
        }

        long s     = System.nanoTime();
        long count = 1000000;

        for (int i = 0; i < count; i++) {
            tc.encode(number);
        }

        long e = System.nanoTime();

        System.out.println(((count * 1000000000) / (e - s)) + " item/s");
    }
}
