package com.pinyougou.manager.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.service.BrandService;

import entity.PageResult;
import entity.Result;

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
	
	/**
	 * @desc 增加品牌列表
	 * @auto 创建人：zzx 
	 * @time 时间：2019年3月28日-上午10:44:04 
	 * @param tbBrand
	 * @return Result
	 */
	@RequestMapping("/saveOrUpdate")
	public Result add(@RequestBody TbBrand brand) {
		if(brand.getId() != null) {
			try {
				brandService.update(brand);
				return new Result(true, "更新成功");
			} catch (Exception e) {
				e.printStackTrace();
				return new Result(false, "更新失败");
			}
		}else {
			try {
				brandService.add(brand);
				return new Result(true, "录入成功") ;
			} catch (Exception e) {
				e.printStackTrace();
				return new Result(false, "录入失败") ;
			}
		}
	}
	
	/**
	 * @desc 通过 id 获取单一品牌信息
	 * @auto 创建人：zzx 
	 * @time 时间：2019年3月28日-下午1:48:19 
	 * @param id
	 * @return TbBrand
	 */
	@RequestMapping("/findOne")
	public TbBrand findOne(Long id) {
		return brandService.findOne(id);
	}
	
	/**
	 * @desc 批量删除品牌信息
	 * @auto 创建人：zzx 
	 * @time 时间：2019年3月28日-下午3:18:38 
	 * @param ids
	 * @return Result
	 */
	@RequestMapping("/delete")
	public Result delete(Long[] ids) {
		try {
			brandService.delete(ids);
			return new Result(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
}
