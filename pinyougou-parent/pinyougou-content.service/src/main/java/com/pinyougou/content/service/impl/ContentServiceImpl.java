package com.pinyougou.content.service.impl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentExample;
import com.pinyougou.pojo.TbContentExample.Criteria;
import com.pinyougou.content.service.ContentService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	
	//引入缓存redis
	@Autowired
	private RedisTemplate redisTemplate;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void add(TbContent content) {
		contentMapper.insert(content);		
		//清除缓存
		redisTemplate.boundHashOps("content").delete(content.getCategoryId());
	}

	
	/**
	 * 修改
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void update(TbContent content){
		//获取分类ID
		Long categoryId = contentMapper.selectByPrimaryKey(content.getId()).getCategoryId();
		//清除缓存
		redisTemplate.boundHashOps("content").delete(categoryId);
		contentMapper.updateByPrimaryKey(content);
		
		//更新后 分类ID 可能发生了变化 更新变化后的分类 ID
		if(categoryId.longValue()!=content.getCategoryId().longValue()) {
			redisTemplate.boundHashOps("content").delete(content.getCategoryId());
		}
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//获取分类 ID
			Long categoryId = contentMapper.selectByPrimaryKey(id).getCategoryId();
			//清除缓存
			redisTemplate.boundHashOps("content").delete(categoryId);
			contentMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

		@SuppressWarnings("unchecked")
		@Override
		public List<TbContent> findByContentCategoryId(Long categoryId) {
			//从缓存汇总获取 如果缓存中不存在则从数据库中获取并添加到缓存
			List<TbContent> selectByExample = (List<TbContent>) redisTemplate.boundHashOps("content").get(categoryId);
			if(selectByExample == null) {
				TbContentExample example = new TbContentExample();
				Criteria criteria = example.createCriteria();
				//封装分类的id
				criteria.andCategoryIdEqualTo(categoryId);
				//查询状态为可用的列表
				criteria.andStatusEqualTo("1");
				//按照排序进行查询
				example.setOrderByClause("sort_order");
				selectByExample = contentMapper.selectByExample(example );
				
				//将结果添加到缓存中
				redisTemplate.boundHashOps("content").put(categoryId, selectByExample);
				System.out.println("从数据库中获取数据");
			}else {
				System.out.println("从缓存中获取数据");
			}
			return selectByExample;
		}
	
}
