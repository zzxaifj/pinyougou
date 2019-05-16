package com.pinyougou.cart.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeixinPayService;

import util.IdWorker;

/**
	 * 支付控制层
	 * @author Administrator
	 *
	 */
	@RestController
	@RequestMapping("/pay")
	public class PayController {
		@Reference
		private  WeixinPayService weixinPayService;
		
		/**
		 * 生成二维码
		 * @return
		 */
		@RequestMapping("/createNative")
		public Map createNative(){
			IdWorker idworker=new IdWorker();		
			return weixinPayService.createNative(idworker.nextId()+"","1");		
		}
	}
