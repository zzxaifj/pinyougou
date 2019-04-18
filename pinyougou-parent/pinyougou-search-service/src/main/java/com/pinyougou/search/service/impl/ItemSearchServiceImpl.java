package com.pinyougou.search.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
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
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Override
	public Map<String, Object> search(Map searchMap) {
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
		
		//查询分类列表
		List<String> categoryList = this.findCategoryList(searchMap);
		map.put("categoryList", categoryList);
		
		//查询品牌列表和规格列表
		String category = (String) searchMap.get("category");
		//如果前台传过来category 分类名称此按照前台取值
		Map brandAndSpecList = new HashMap<>();
		if(!"".equals(category)) {
			brandAndSpecList = this.searchBrandAndSpecList(category);
		//如果前台为空 则去分类的第一个分类获取品牌和规格
		}else if(categoryList.size()>0){
			brandAndSpecList = this.searchBrandAndSpecList(categoryList.get(0));
		}
		//将品牌和规格进行封装
		map.putAll(brandAndSpecList);
		return map;
	}

	private Map<String, Object> findHighLightQuery(Map searchMap) {
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
		
		//添加分类 过滤条件
		if(!"".equals(searchMap.get("category"))) {
			Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
			FilterQuery filterQuery  = new SimpleFilterQuery(filterCriteria );
			highlightQuery.addFilterQuery(filterQuery  );
		}
		//添加品牌
		if( !"".equals(searchMap.get("brand"))) {
			Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
			FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria );
			highlightQuery.addFilterQuery(filterQuery );
		}
		Object object = searchMap.get("spec");
		//添加规格
		if(object!=null) {
			Map<String, String> map = (Map)searchMap.get("spec");
			//循环规格对象
			for(String key:map.keySet()) {
				Criteria specCriteria = new Criteria("item_spec_"+key).is(map.get(key));
				FilterQuery filterQuery = new SimpleFilterQuery(specCriteria );
				highlightQuery.addFilterQuery(filterQuery );
			}
		}	
		//查询 solr
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

	/**
	 * @desc 查询分类列表
	 * @auto 创建人：zzx 
	 * @time 时间：2019年4月17日-下午12:28:32 
	 * @param map
	 * @return List<TbItem>
	 * @exception
	 */
	private List<String> findCategoryList(Map<String, String> map) {
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
	
	@SuppressWarnings("unchecked")
	private Map searchBrandAndSpecList(String CategoryName) {
		Map map = new HashMap<>();
		//获取 模板 ID
		Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(CategoryName);
		
		//获取品牌信息
		List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
		map.put("brandList", brandList);
		
		//获取规格信息
		List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
		map.put("specList", specList);
		return map;
	}

}
