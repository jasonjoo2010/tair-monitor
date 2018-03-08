package com.taobao.common.tair.comm;

/**
 * 对象编码器
 * 在将对象编码后，除了原有的数据外，会在头部加一个int信息，这个用来标识被编码数据的格式等信息。
 * <table border=1>
 * <tr>
 * <td>000000000000000000000000000</td>
 * <td>0000</td>
 * <td>0</td>
 * </tr>
 * <tr>
 * <td>保留位，用于以后的扩展</td>
 * <td>表示编码前对象的类型，参加TairConstant中关于类型的定义</td>
 * <td>是否压缩，1标识被压缩，0标识未压缩</td>
 * </tr>
 * </table>
 * 
 * @author ruohai
 * 
 */
public interface Transcoder { 

	/**
	 * 将对象编码成byte数组
	 * 
	 * @param object
	 * @return
	 */
	byte[] encode(Object object);

	/**
	 * 将对象解码成原来的对象
	 * 
	 * @param data
	 * @return
	 */
	Object decode(byte[] data);
	/**
	 * 将对象解码成原来的对象
	 * 
	 * @param data
	 * @return
	 */
	Object decode(byte[] data, int offset, int size);
}