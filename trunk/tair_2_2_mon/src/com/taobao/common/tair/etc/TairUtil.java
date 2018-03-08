package com.taobao.common.tair.etc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.net.InetSocketAddress;

import java.util.zip.InflaterInputStream;

public class TairUtil {
    /**
     * 检查分配的内存大小是否超过限定值
     */
    public static void checkMalloc(int size) {
        if (size > TairConstant.TAIR_MALLOC_MAX) {
            throw new IllegalArgumentException("alloc to large byte[], size: " + size);
        }
    }

    public static byte[] deflate(byte[] in) {
        ByteArrayOutputStream bos = null;

        if (in != null) {
            ByteArrayInputStream bis = new ByteArrayInputStream(in);

            bos = new ByteArrayOutputStream();

            InflaterInputStream gis;

            try {
                gis = new InflaterInputStream(bis);

                byte[] buf = new byte[8192];
                int    r   = -1;

                while ((r = gis.read(buf)) > 0) {
                    bos.write(buf, 0, r);
                }
            } catch (IOException e) {
                bos = null;
            }
        }

        return (bos == null) ? null
                             : bos.toByteArray();
    }


    public static String idToAddress(long id) {
        StringBuffer host = new StringBuffer(30);

        host.append((id & 0xff)).append('.');
        host.append(((id >> 8) & 0xff)).append('.');
        host.append(((id >> 16) & 0xff)).append('.');
        host.append(((id >> 24) & 0xff));

        int port = (int) ((id >> 32) & 0xffff);

        return host.append(":").append(port).toString();
    }
    
    public static long setServerDown(long id) {
    	return id | (1L << 49);
    }
    
    public static long setServerUp(long id) {
    	return id & ~(1L << 49);
    }
    
    public static boolean isServerUp(long id) {
    	return (id & (1L << 49)) == 0;
    }

    /**
     * 把host(ip:port)转成long
     *
     * @param address
     *
     * @return
     */
    public static long hostToLong(String host) {
        return hostToLong(host, -1);
    }
    
    public static String getHost(String address) {
		String host = null;
		if (address != null) {
			String[] a = address.split(":");
			if (a.length >= 2)
				host = a[0].trim();
		}
		return host;
	}
    
    public static int getPort(String address) {
		int port = 0;
		if (address != null) {
			String[] a = address.split(":");
			if (a.length >= 2)
				port = Integer.parseInt(a[1].trim());
		}
		return port;
	}

    public static long hostToLong(String host, int port) {
        if (host == null) {
            return 0;
        }

        try {
            String[] a = host.split(":");

            if (a.length >= 2) {
                port = Integer.parseInt(a[1].trim());
            }

            if (port == -1) {
                return 0;
            }

            InetSocketAddress addr = new InetSocketAddress(a[0], port);

            if ((addr == null) || (addr.getAddress() == null) || (addr.getPort() == 0)) {
                return 0;
            }

            byte[] ip      = addr.getAddress().getAddress();
            long   address = (addr.getPort() & 0xffff);

            
            int ipa = 0;
            ipa |= ((ip[3] << 24) & 0xff000000);
            ipa |= ((ip[2] << 16) & 0xff0000);
            ipa |= ((ip[1] << 8) & 0xff00);
            ipa |= (ip[0] & 0xff);
            
            if(ipa < 0) address +=1;
            address <<= 32;
            return address + ipa;
        } catch (Exception e) {
        }

        return 0;
    }
}
