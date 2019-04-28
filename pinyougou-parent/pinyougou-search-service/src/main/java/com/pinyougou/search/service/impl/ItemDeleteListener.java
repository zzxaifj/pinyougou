package com.pinyougou.search.service.impl;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pinyougou.search.service.ItemSearchService;

@Component
public class ItemDeleteListener implements MessageListener{

	@Autowired
	private ItemSearchService itemSearchService;
	
	@Override
	public void onMessage(Message message) {
		try {
			//获取传过来的参数 既从队列中获取值
			ObjectMessage objMessage = (ObjectMessage) message;
			Long[] ids = (Long[]) objMessage.getObject();
			//进行solr 中删除
			itemSearchService.deleteByGoodsIds(ids);
			System.out.println("通过 JMS 删除solr 数据");
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}
