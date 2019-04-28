package com.pinyougou.search.service.impl;

import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
@Component
public class ItemSearchListener implements MessageListener{

	@Autowired
	private ItemSearchService itemSearchService;
	
	@Override
	public void onMessage(javax.jms.Message message) {
		try {
			//获取监听的消息
			TextMessage textMessage = (TextMessage) message;
			String text = textMessage.getText();
			//转化为 list<TbItem> 集合
			List<TbItem> items = JSON.parseArray(text, TbItem.class);
			//将spec 的字符串转为 map 对象
			for(TbItem item:items) {
				Map spec = JSON.parseObject(item.getSpec(), Map.class);//动态的域赋值
				item.setSpecMap(spec);
			}
			//导入到solr中
			itemSearchService.importList(items);
			System.out.println("通过 JMS 插入solr 数据");
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}
