package com.pinyougou.page.service.impl;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pinyougou.page.service.ItemPageService;
@Component
public class PageDeleteListener implements MessageListener {
	@Value("${pagedir}")
	private String pagedir;
	
	@Autowired
	private ItemPageService itemPageService;
	
	@Override
	public void onMessage(Message message) {
		try {
			//从队列中获取数据
			ObjectMessage objectMessage = (ObjectMessage) message;
			Long[] ids = (Long[]) objectMessage.getObject();
			//删除模板
			itemPageService.delItemHtml(ids);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}
