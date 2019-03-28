package com.pinyougou.service;

import java.util.List;

import com.pinyougou.pojo.TbBrand;

import entity.PageResult;

public interface BrandService {
	
	/**
	 * @desc 查询所有品牌列表
	 * @auto 创建人：zzx 
	 * @time 时间：2019年3月27日-下午10:54:25 
	 * @return List<TbBrand>
	 */
	public List<TbBrand> findAll();
	
	/**
	 * @desc  分页查询品牌列表
	 * @auto 创建人：zzx 
	 * @time 时间：2019年3月27日-下午10:56:11 
	 * @param pageNum:页码
	 * @param pageSize：每页的个数
	 * @return PageResult
	 */
	public PageResult findPage(int pageNum,int pageSize);
}
