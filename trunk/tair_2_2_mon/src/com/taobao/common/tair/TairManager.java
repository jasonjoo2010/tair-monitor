/**
 * 
 */
package com.taobao.common.tair;

import java.io.Serializable;
import java.util.List;

/**
 * Tair的接口，支持持久化存储和非持久化（即cache）存储
 * @author ruohai
 * 
 */
public interface TairManager {

	/**
	 * 获取数据
	 * @param namespace 数据所在的namespace
	 * @param key 要获取的数据的key
	 * @return
	 */
	Result<DataEntry> get(int namespace, Object key);

	/**
	 * 批量获取数据
	 * @param namespace 数据所在的namespace
	 * @param keys 要获取的数据的key列表
	 * @return 如果成功，返回的数据对象为一个Map<Key, Value>
	 */
	Result<List<DataEntry>> mget(int namespace, List<Object> keys);
	
	/**
	 * 设置数据，如果数据已经存在，则覆盖，如果不存在，则新增
	 * 如果是新增，则有效时间为0，即不失效
	 * 如果是更新，则不检查版本，强制更新
	 * @param namespace 数据所在的namespace
	 * @param key
	 * @param value
	 * @return
	 */
	ResultCode put(int namespace, Object key, Serializable value);

	/**
	 * 设置数据，如果数据已经存在，则覆盖，如果不存在，则新增
	 * @param namespace 数据所在的namespace
	 * @param key 数据的key
	 * @param value 数据的value
	 * @param version 数据的版本，如果和系统中数据的版本不一致，则更新失败
	 * @return
	 */	
	ResultCode put(int namespace, Object key, Serializable value, int version);
	
	/**
	 * 设置数据，如果数据已经存在，则覆盖，如果不存在，则新增
	 * @param namespace 数据所在的namespace
	 * @param key 数据的key
	 * @param value 数据的value
	 * @param version 数据的版本，如果和系统中数据的版本不一致，则更新失败
	 * @param expireTime 数据的有效时间，单位为秒
	 * @return
	 */	
	ResultCode put(int namespace, Object key, Serializable value, int version, int expireTime);

	/**
	 * 删除key对应的数据
	 * @param namespace 数据所在的namespace
	 * @param key 数据的key
	 * @return
	 */
	ResultCode delete(int namespace, Object key);
	
	/**
	 * 失效数据，该方法将失效由失效服务器配置的多个实例中当前group下的数据
	 * @param namespace 数据所在的namespace
	 * @param key 要失效的key
	 * @return
	 */
	ResultCode invalid(int namespace, Object key);
	
	/**
	 * 批量失效数据，该方法将失效由失效服务器配置的多个实例中当前group下的数据
	 * @param namespace 数据所在的namespace
	 * @param keys 要失效的key列表
	 * @return
	 */
	ResultCode minvalid(int namespace, List<? extends Object> keys);

	/**
	 * 批量删除，如果全部删除成功，返回成功，否则返回失败
	 * @param namespace 数据所在的namespace
	 * @param keys 要删除数据的key列表
	 * @return 
	 */
	ResultCode mdelete(int namespace, List<Object> keys);

	/**
	 * 将key对应的数据加上value，如果key对应的数据不存在，则新增，并将值设置为defaultValue
	 * 如果key对应的数据不是int型，则返回失败
	 * @param namespace 数据所在的namspace
	 * @param key 数据的key
	 * @param value 要加的值
	 * @param defaultValue 不存在时的默认值
	 * @return 更新后的值
	 */
	Result<Integer> incr(int namespace, Object key, int value, int defaultValue);

	/**
	 * 将key对应的数据减去value，如果key对应的数据不存在，则新增，并将值设置为defaultValue
	 * 如果key对应的数据不是int型，则返回失败
	 * @param namespace 数据所在的namspace
	 * @param key 数据的key
	 * @param value 要减去的值
	 * @param defaultValue 不存在时的默认值
	 * @return 更新后的值
	 */
	Result<Integer> decr(int namespace, Object key, int value, int defaultValue);
	
	/** 
	 * 获取客户端的版本 
	 */
	String getVersion();
}
