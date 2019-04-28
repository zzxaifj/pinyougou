package com.pinyougou.search.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
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
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;

import com.alibaba.dubbo.config.annotation.Service;
import com.fasterxml.jackson.databind.deser.impl.ManagedReferenceProperty;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;

@Service(timeout=3000)
public class ItemSearchServiceImpl implements ItemSearchService{
	
	@Autowired
	private SolrTemplate solrTemplate;
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> search(Map searchMap) {
		
		//如果keyword 包涵空格 会影响分词器  分词  因此要替换掉
		String keywords = (String) searchMap.get("keywords");
		searchMap.put("keywords", keywords.replace(" ", ""));
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
		//高亮查询  === 排序  
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
			@SuppressWarnings("unchecked")
			Map<String, String> map = (Map)searchMap.get("spec");
			//循环规格对象
			for(String key:map.keySet()) {
				Criteria specCriteria = new Criteria("item_spec_"+key).is(map.get(key));
				FilterQuery filterQuery = new SimpleFilterQuery(specCriteria );
				highlightQuery.addFilterQuery(filterQuery );
			}
		}	
		
		//根据价格区间查询数据
		if(!"".equals(searchMap.get("price"))) {
			//拆分 0-500 
			String[] prices = ((String) searchMap.get("price")).split("-");
			
			//排除 最大价格的特殊情况 大于最小值
			Criteria priCriteria = new Criteria("item_price").greaterThanEqual(prices[0]);
			//小于最大值  除了最后一种特殊情况    两边都选等于  是因为希望客户能够多查到  就有多购买的情况 是商家最想看到的  因此两边取等于
			FilterQuery filterQuery = new SimpleFilterQuery(priCriteria );
			highlightQuery.addFilterQuery(filterQuery );
			
			if( !"*".equals(prices[1])) {
				Criteria priCriteria1 = new Criteria("item_price").lessThanEqual(prices[1]);
				FilterQuery filterQuery1 = new SimpleFilterQuery(priCriteria1);
				highlightQuery.addFilterQuery(filterQuery1);
			}
		}
		
		//分页查询
		this.findItemForPage(searchMap, highlightQuery);
		
		String order = (String) searchMap.get("sort");
		String field = (String) searchMap.get("field");
		if(!"".equals(order)&&!"".equals(field)) {
			if("ASC".equals(order)) {
				Sort sort = new Sort(Sort.Direction.ASC, "item_"+field);
				//按照价格  最新时间更新
				highlightQuery.addSort(sort );
			}
			if("DESC".equals(order)) {
				Sort sort = new Sort(Sort.Direction.DESC, "item_"+field);
				//按照价格  最新时间更新
				highlightQuery.addSort(sort );
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
			if(h.getHighlights().size()>0&&h.getHighlights().get(0).getSnipplets().size()>0) {
				String title = h.getHighlights().get(0).getSnipplets().get(0);
				item.setTitle(title);
			}
		}
		
		Map<String, Object> map = new HashMap<>();
		map.put("rows", highlightPage.getContent());
		//总记录数
		map.put("total", highlightPage.getTotalElements());
		//总页数
		map.put("totalPage", highlightPage.getTotalPages());
		return map;
	}

	/**
	 * @desc 分页查询  pageNo=offSet其实值 pageSize = rows 每页的个数  
	 * @auto 创建人：zzx 
	 * @time 时间：2019年4月20日-上午9:37:45 
	 * @param searchMap  前台参数
	 * @param highlightQuery void 后台传入的对象
	 */
	private void findItemForPage(Map searchMap, SimpleHighlightQuery highlightQuery) {
		//分页查询   如果为空或者为0需要赋初始值
		Integer pageNo = (Integer) searchMap.get("pageNo");
		Integer pageSize = (Integer) searchMap.get("pageSize");
		int pageNoInt=1;
		int pageSizeInt=40;
		if(pageNo != null && pageNo!=0) {
			pageNoInt = pageNo;
		}
		if(pageSize != null && pageSize!=0) {
			pageSizeInt = pageSize;
		}
		//添加到 solr 分页参数
		highlightQuery.setOffset((pageNoInt-1)*pageSizeInt);
		highlightQuery.setRows(pageSizeInt);
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

	@Override
	public void importList(List list) {
		solrTemplate.saveBeans(list);
		solrTemplate.commit();
	}

	@Override
	public void deleteByGoodsIds(Long[] ids) {
		List<Long> asList = Arrays.asList(ids);
		Criteria criteria=new Criteria("item_goodsid").in(asList);
		Query query = new SimpleQuery(criteria);
		solrTemplate.delete(query );
		solrTemplate.commit();
	}

}
