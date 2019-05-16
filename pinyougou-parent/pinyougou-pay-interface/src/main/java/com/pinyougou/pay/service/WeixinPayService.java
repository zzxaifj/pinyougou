package com.pinyougou.pay.service;
/**
 * 微信支付接口
 * 创建人:zzx
 * 时间：2019年5月16日-下午2:11:43 
 * @version 1.0.0
 *
 */

import java.util.Map;

public interface WeixinPayService {
	/**
	 * @desc 生成微信支付二维码
	 * @auto 创建人：zzx 
	 * @time 时间：2019年5月16日-下午2:15:34 
	 * @param out_trade_no  订单号 
	 * @param total_fee  金额
	 * @return Map
	 * @exception
	 */
	public Map createNative(String out_trade_no,String total_fee);
}
