package com.pinyougou.page.service;

public interface ItemPageService {
	/**
	 * @desc 通过 goodsId 生成html
	 * @auto 创建人：zzx 
	 * @time 时间：2019年4月26日-下午5:21:32 
	 * @param goodsId
	 * @return boolean
	 * @exception
	 */
	public boolean genItemHtml(Long goodsId);
	
	public boolean delItemHtml(Long[] ids);
}
