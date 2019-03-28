package com.pinyougou.sellergoods.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.service.BrandService;

import entity.PageResult;

@Service
public class BrandServiceImpl implements BrandService {
	
	@Autowired
	private TbBrandMapper brandMapper;

	@Override
	public List<TbBrand> findAll() {
		return brandMapper.selectByExample(null);
	}
	
	/**
	 * @desc 分页查询的实现
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void add(TbBrand tbBrand) {
		brandMapper.insert(tbBrand);
	}

	@Override
	public void update(TbBrand tbBrand) {
		brandMapper.updateByPrimaryKey(tbBrand);
	}

	@Override
	public TbBrand findOne(Long id) {
		return brandMapper.selectByPrimaryKey(id);
	}

	@Override
	public void delete(Long[] ids) {
		for(Long id:ids) {
			brandMapper.deleteByPrimaryKey(id);
		}
	}

}
