package com.pinyougou.search.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.search.service.ItemSearchService;

@RestController
@RequestMapping("/itemsearch")
public class ItemSearchController {
	
	@Reference
	private ItemSearchService itemSearchService;
	
	@RequestMapping("/search")
	public Map<String, Object> search(@RequestBody Map<String, String> searchMap){
		Map<String, Object> map = new HashMap<>();
		//高亮显示
		map.putAll(itemSearchService.search(searchMap));
		//对查询结果进行分组,并返回
		map.put("categoryList", itemSearchService.findCategoryList(searchMap));
		return map;
	}
}
