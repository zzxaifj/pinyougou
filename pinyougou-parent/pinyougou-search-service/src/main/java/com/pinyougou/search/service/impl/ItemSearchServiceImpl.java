package com.pinyougou.search.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;

@Service(timeout=3000)
public class ItemSearchServiceImpl implements ItemSearchService{
	
	@Autowired
	private SolrTemplate solrTemplate;
	
	@Override
	public Map<String, Object> search(Map<String,String> searchMap) {
		/* 正常实现如下
		 Query query = new SimpleQuery();
		//封装查询条件   查询复制域
		Criteria criteria = new Criteria("item_keywords")
				.is(searchMap.get("keywords"));
		query.addCriteria(criteria);
		ScoredPage<TbItem> page = solrTemplate.queryForPage(query , TbItem.class);
		Map<String, Object> map = new HashMap<>();
		map.put("rows", page.getContent());*/
		Map<String, Object> map = new HashMap<>();
		//发亮查询
		map.putAll(findHighLightQuery(searchMap));
		return map;
	}

	private Map<String, Object> findHighLightQuery(Map<String, String> searchMap) {
		//搜索的关键字 高亮显示
		SimpleHighlightQuery highlightQuery = new SimpleHighlightQuery();
		//首先要传入需要高亮显示的域
		HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
		//高亮前缀
		highlightOptions.setSimplePrefix("<em style='color:red'>");
		//高亮后缀
		highlightOptions.setSimplePostfix("</em>");
		//将高亮属性赋值到 查询条件中
		highlightQuery.setHighlightOptions(highlightOptions);
		//封装查询条件   查询复制域
		Criteria criteria = new Criteria("item_keywords")
				.is(searchMap.get("keywords"));
		highlightQuery.addCriteria(criteria);
		HighlightPage<TbItem> highlightPage = solrTemplate.queryForHighlightPage(highlightQuery, TbItem.class);
		
		
		//循环 高亮入口集合  highlightPage.getHighlighted()
		for(HighlightEntry<TbItem> h : highlightPage.getHighlighted()) {
			//获取原实体
			TbItem item = h.getEntity();
			//循环需要高亮的域
			/*for(Highlight hight:h.getHighlights()) {
				//每个域都有多个值 多个域的循环
				for(hight.getSnipplets()) {
					
				}
			}*/
			//因为我们只有一个域 也不存在多值因此只获取第一个
			String string = h.getHighlights().get(0).getSnipplets().get(0);
			item.setTitle(string);
		}
		
		Map<String, Object> map = new HashMap<>();
		map.put("rows", highlightPage.getContent());
		return map;
	}

	@Override
	public List<String> findCategoryList(Map<String, String> map) {
		Query query = new SimpleQuery("*:*");
		//通过页面传过来的key进行查询
		Criteria criteria = new Criteria("item_keywords").is(map.get("keywords"));
		query.addCriteria(criteria );
		//封装分组域
		GroupOptions groupOptions = new GroupOptions();
		groupOptions.addGroupByField("item_category");
		query.setGroupOptions(groupOptions);
		//从 solr 中进行分组查询 queryFor GROUP page 
		GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query , TbItem.class);
		//获取根据那个域查询的分组集合  因为可以通过多个域分组查询
		GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
		//得到结果集的入口
		Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
		
		//返回的 list 构建
		List<String> list = new ArrayList<>();
		for(GroupEntry<TbItem> item: groupEntries) {
			list.add(item.getGroupValue());
		}
		
		return list;
	}

}
