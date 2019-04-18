package com.pinyougou.search.service;

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
	
}
