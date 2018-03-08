/**
 * 
 */
package com.taobao.common.tair;

import java.io.Serializable;
import java.util.List;

/**
 * Tair�Ľӿڣ�֧�ֳ־û��洢�ͷǳ־û�����cache���洢
 * @author ruohai
 * 
 */
public interface TairManager {

	/**
	 * ��ȡ����
	 * @param namespace �������ڵ�namespace
	 * @param key Ҫ��ȡ�����ݵ�key
	 * @return
	 */
	Result<DataEntry> get(int namespace, Object key);

	/**
	 * ������ȡ����
	 * @param namespace �������ڵ�namespace
	 * @param keys Ҫ��ȡ�����ݵ�key�б�
	 * @return ����ɹ������ص����ݶ���Ϊһ��Map<Key, Value>
	 */
	Result<List<DataEntry>> mget(int namespace, List<Object> keys);
	
	/**
	 * �������ݣ���������Ѿ����ڣ��򸲸ǣ���������ڣ�������
	 * ���������������Чʱ��Ϊ0������ʧЧ
	 * ����Ǹ��£��򲻼��汾��ǿ�Ƹ���
	 * @param namespace �������ڵ�namespace
	 * @param key
	 * @param value
	 * @return
	 */
	ResultCode put(int namespace, Object key, Serializable value);

	/**
	 * �������ݣ���������Ѿ����ڣ��򸲸ǣ���������ڣ�������
	 * @param namespace �������ڵ�namespace
	 * @param key ���ݵ�key
	 * @param value ���ݵ�value
	 * @param version ���ݵİ汾�������ϵͳ�����ݵİ汾��һ�£������ʧ��
	 * @return
	 */	
	ResultCode put(int namespace, Object key, Serializable value, int version);
	
	/**
	 * �������ݣ���������Ѿ����ڣ��򸲸ǣ���������ڣ�������
	 * @param namespace �������ڵ�namespace
	 * @param key ���ݵ�key
	 * @param value ���ݵ�value
	 * @param version ���ݵİ汾�������ϵͳ�����ݵİ汾��һ�£������ʧ��
	 * @param expireTime ���ݵ���Чʱ�䣬��λΪ��
	 * @return
	 */	
	ResultCode put(int namespace, Object key, Serializable value, int version, int expireTime);

	/**
	 * ɾ��key��Ӧ������
	 * @param namespace �������ڵ�namespace
	 * @param key ���ݵ�key
	 * @return
	 */
	ResultCode delete(int namespace, Object key);
	
	/**
	 * ʧЧ���ݣ��÷�����ʧЧ��ʧЧ���������õĶ��ʵ���е�ǰgroup�µ�����
	 * @param namespace �������ڵ�namespace
	 * @param key ҪʧЧ��key
	 * @return
	 */
	ResultCode invalid(int namespace, Object key);
	
	/**
	 * ����ʧЧ���ݣ��÷�����ʧЧ��ʧЧ���������õĶ��ʵ���е�ǰgroup�µ�����
	 * @param namespace �������ڵ�namespace
	 * @param keys ҪʧЧ��key�б�
	 * @return
	 */
	ResultCode minvalid(int namespace, List<? extends Object> keys);

	/**
	 * ����ɾ�������ȫ��ɾ���ɹ������سɹ������򷵻�ʧ��
	 * @param namespace �������ڵ�namespace
	 * @param keys Ҫɾ�����ݵ�key�б�
	 * @return 
	 */
	ResultCode mdelete(int namespace, List<Object> keys);

	/**
	 * ��key��Ӧ�����ݼ���value�����key��Ӧ�����ݲ����ڣ�������������ֵ����ΪdefaultValue
	 * ���key��Ӧ�����ݲ���int�ͣ��򷵻�ʧ��
	 * @param namespace �������ڵ�namspace
	 * @param key ���ݵ�key
	 * @param value Ҫ�ӵ�ֵ
	 * @param defaultValue ������ʱ��Ĭ��ֵ
	 * @return ���º��ֵ
	 */
	Result<Integer> incr(int namespace, Object key, int value, int defaultValue);

	/**
	 * ��key��Ӧ�����ݼ�ȥvalue�����key��Ӧ�����ݲ����ڣ�������������ֵ����ΪdefaultValue
	 * ���key��Ӧ�����ݲ���int�ͣ��򷵻�ʧ��
	 * @param namespace �������ڵ�namspace
	 * @param key ���ݵ�key
	 * @param value Ҫ��ȥ��ֵ
	 * @param defaultValue ������ʱ��Ĭ��ֵ
	 * @return ���º��ֵ
	 */
	Result<Integer> decr(int namespace, Object key, int value, int defaultValue);
	
	/** 
	 * ��ȡ�ͻ��˵İ汾 
	 */
	String getVersion();
}
