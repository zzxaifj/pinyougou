package com.pinyougou.manager.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.service.BrandService;

import entity.PageResult;

@RestController
@RequestMapping("/brand")
public class BrandController {
	
	@Reference
	private BrandService brandService;
	
	@RequestMapping("/findAll")
	public List<TbBrand> findAll(){
		return brandService.findAll();
	}
	
	/**
	 * @desc 分页查询
	 * @auto 创建人：zzx 
	 * @time 时间：2019年3月27日-下午11:22:07 
	 * @param pageNum 页码
	 * @param pageSize 每页记录数
	 * @return PageResult 返回值 封装了总记录数和结果集
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page,int rows) {
		return brandService.findPage(page, rows);
	}
}
