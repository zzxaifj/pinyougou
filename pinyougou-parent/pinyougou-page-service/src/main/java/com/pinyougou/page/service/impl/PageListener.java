package com.pinyougou.page.service.impl;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pinyougou.page.service.ItemPageService;
@Component
public class PageListener implements MessageListener{
	
	@Autowired
	private ItemPageService itemPageService;

	@Override
	public void onMessage(Message message) {
		try {
			//获取队列中的数据
			TextMessage textMessage = (TextMessage) message;
			String id = textMessage.getText();
			Long goodsId = Long.parseLong(id);
			//生成模板
			itemPageService.genItemHtml(goodsId);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
}
