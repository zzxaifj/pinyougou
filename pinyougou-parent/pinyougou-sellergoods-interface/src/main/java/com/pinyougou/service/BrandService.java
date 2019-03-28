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
	
	/**
	 * @desc 添加品牌列表
	 * @auto 创建人：zzx 
	 * @time 时间：2019年3月28日-上午10:34:16 
	 * @param tbBrand void
	 */
	public void add(TbBrand tbBrand);
	
	/**
	 * @desc 修改品牌列表
	 * @auto 创建人：zzx 
	 * @time 时间：2019年3月28日-下午1:30:59  void
	 */
	public void update(TbBrand tbBrand);
	
	/**
	 * @desc 通过 id 获取单一品牌对象
	 * @auto 创建人：zzx 
	 * @time 时间：2019年3月28日-下午1:46:14 
	 * @param id
	 * @return TbBrand
	 */
	public TbBrand findOne(Long id);
	
	/**
	 * @desc 通过id集合轮序删除品牌信息
	 * @auto 创建人：zzx 
	 * @time 时间：2019年3月28日-下午3:11:33 
	 * @param ids void
	 */
	public void delete(Long[] ids);
	
	/**
	 * @desc 模糊查询 品牌 列表
	 * @auto 创建人：zzx 
	 * @time 时间：2019年3月28日-下午4:36:11 
	 * @param tbBrand
	 * @return PageResult
	 */
	public PageResult search(TbBrand tbBrand,int pageNum,int pageSize);
}
