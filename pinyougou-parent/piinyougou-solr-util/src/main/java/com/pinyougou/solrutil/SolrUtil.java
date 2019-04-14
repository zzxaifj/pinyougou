package com.pinyougou.solrutil;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbItemExample.Criteria;

@Component
public class SolrUtil {
	
	@Autowired
	private TbItemMapper itemMapper;
	
	@Autowired
	private SolrTemplate solrTemplate;
	
	public void importItemToSolr() {
		TbItemExample example = new TbItemExample();
		Criteria criteria = example.createCriteria();
		//只获取审核通过的数据
		criteria.andStatusEqualTo("1");
		List<TbItem> list = itemMapper.selectByExample(example);
		
		//动态域的复制
		for(TbItem item : list) {
		    Map specMap = JSON.parseObject(item.getSpec(), Map.class);
		    item.setSpecMap(specMap);
		    //测试使用
		    System.out.println(item.getTitle());
		}
		
		//将数据插入 solr
		solrTemplate.saveBeans(list);
		solrTemplate.commit();
	}
	
	//jar 类型的项目要有一个入口
	public static void main(String[] args) {
		//传统的方式获取 配置文件
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
		//获取bean
		SolrUtil solr = (SolrUtil) context.getBean("solrUtil");
		//导入数据
		solr.importItemToSolr();
	}
}
