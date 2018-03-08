package com.taobao.common.tair.comm;

/**
 * ���������
 * �ڽ��������󣬳���ԭ�е������⣬����ͷ����һ��int��Ϣ�����������ʶ���������ݵĸ�ʽ����Ϣ��
 * <table border=1>
 * <tr>
 * <td>000000000000000000000000000</td>
 * <td>0000</td>
 * <td>0</td>
 * </tr>
 * <tr>
 * <td>����λ�������Ժ����չ</td>
 * <td>��ʾ����ǰ��������ͣ��μ�TairConstant�й������͵Ķ���</td>
 * <td>�Ƿ�ѹ����1��ʶ��ѹ����0��ʶδѹ��</td>
 * </tr>
 * </table>
 * 
 * @author ruohai
 * 
 */
public interface Transcoder { 

	/**
	 * ����������byte����
	 * 
	 * @param object
	 * @return
	 */
	byte[] encode(Object object);

	/**
	 * ����������ԭ���Ķ���
	 * 
	 * @param data
	 * @return
	 */
	Object decode(byte[] data);
	/**
	 * ����������ԭ���Ķ���
	 * 
	 * @param data
	 * @return
	 */
	Object decode(byte[] data, int offset, int size);
}