package com.pinyougou.search.service;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {
	/**
	 * @desc 模糊查询  当后台没有对应对象接受的时候就使用map接受
	 * @auto 创建人：zzx 
	 * @time 时间：2019年4月17日-下午12:28:50 
	 * @param map
	 * @return Map<String,Object>
	 * @exception
	 */
	public Map<String, Object> search(Map map);
	
	/**
	 * @desc 数据导入
	 * @auto 创建人：zzx 
	 * @time 时间：2019年4月20日-下午4:38:57 
	 * @param list void
	 * @exception
	 */
	public void importList(List list);
	
	/**
	 * @desc 删除商品时 从solr 中消除商品
	 * @auto 创建人：zzx 
	 * @time 时间：2019年4月20日-下午5:04:40 
	 * @param list void
	 * @exception
	 */
	public void deleteByGoodsIds(Long[] ids);
}
