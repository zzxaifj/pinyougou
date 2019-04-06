package com.pinyougou.pojogroup;

import java.io.Serializable;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;

public class Goods implements Serializable{
	private TbGoods tbGoods;
	private TbGoodsDesc tbGoodsDesc;
	public TbGoods getTbGoods() {
		return tbGoods;
	}
	public void setTbGoods(TbGoods tbGoods) {
		this.tbGoods = tbGoods;
	}
	public TbGoodsDesc getTbGoodsDesc() {
		return tbGoodsDesc;
	}
	public void setTbGoodsDesc(TbGoodsDesc tbGoodsDesc) {
		this.tbGoodsDesc = tbGoodsDesc;
	}
	
	
}
