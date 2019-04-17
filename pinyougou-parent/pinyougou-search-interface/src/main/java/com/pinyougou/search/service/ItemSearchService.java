package com.pinyougou.search.service;

import java.util.Map;

public interface ItemSearchService {
	//模糊查询  当后台没有对应对象接受的时候就使用map接受
	public Map<String, Object> search(Map<String,String> map);
}
