package com.pinyougou.pay.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeixinPayService;

import util.HttpClient;

public class WeixinPayServiceImpl implements WeixinPayService{
	
	@Value("appid")
	private String appid;
	
	@Value("partner")
	private String partner;
	
	@Value("${partnerkey}")
	private String partnerkey;
	
	/**
	 * 生成二维码
	 */
	@Override
	public Map createNative(String out_trade_no, String total_fee) {
		//1、创建参数
		Map<String, String> param = new HashMap<>();
		param.put("appid", appid);//公众号
		param.put("mch_id", partner);//商户号
		param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串		
		param.put("body", "品优购");//商品描述
		param.put("out_trade_no", out_trade_no);//商户订单号
		param.put("total_fee",total_fee);//总金额（分）
		param.put("spbill_create_ip", "127.0.0.1");//IP
		param.put("notify_url", "http://test.itcast.cn");//回调地址(随便写)
		param.put("trade_type", "NATIVE");//交易类型
		try {
			String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
			System.out.println("请求的参数："+xmlParam);
			
			//2.发送请求			
			HttpClient httpClient=new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
			httpClient.setHttps(true);
			httpClient.setXmlParam(xmlParam);
			httpClient.post();
			
			//3.获取结果
			String xmlResult = httpClient.getContent();
			
			Map<String, String> mapResult = WXPayUtil.xmlToMap(xmlResult);
			System.out.println("微信返回结果"+mapResult);
			Map map=new HashMap<>();
			map.put("code_url", mapResult.get("code_url"));//生成支付二维码的链接
			map.put("out_trade_no", out_trade_no);
			map.put("total_fee", total_fee);
			
			return map;
			
		} catch (Exception e) {
			e.printStackTrace();
			return new HashMap();
		}
	}

}
