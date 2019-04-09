package com.pinyougou.sellergoods.service.impl;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.mapper.TbSellerMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbGoodsExample;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.service.GoodsService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbBrandMapper brandMapper;
	@Autowired
	private TbSellerMapper sellerMapper;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		TbGoodsExample goods = new TbGoodsExample();
		Criteria criteria = goods.createCriteria();
		criteria.andIsDeleteIsNull();
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(goods );
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		//状态为 0
		goods.getTbGoods().setAuditStatus("0");
		goodsMapper.insert(goods.getTbGoods());		
		//将商品id 赋值给附属信息中 在xml中需要设置
		goods.getTbGoodsDesc().setGoodsId(goods.getTbGoods().getId());
		goodsDescMapper.insert(goods.getTbGoodsDesc());
		
		this.createItemInfo(goods);
	}

	private void createItemInfo(Goods goods) {
		//是否启用规格
		if("1".equals(goods.getTbGoods().getIsEnableSpec())) {
			//插入 item 商品表中
			for(TbItem item:goods.getItemList()) {
				//stu商品标题 = spu商品标题+规格
				String title = goods.getTbGoods().getGoodsName();
				Map<String, Object> specMap = JSON.parseObject(item.getSpec());
				for(String key:specMap.keySet()) {
					title+=" "+specMap.get(key);
				}
				item.setTitle(title);
				
				setItemValues(goods, item);
				
				itemMapper.insert(item);
			}
		}else {
			TbItem tbItem = new TbItem();
			//标题
			tbItem.setTitle(goods.getTbGoods().getGoodsName());
			//设置价格
			tbItem.setPrice(goods.getTbGoods().getPrice());
			//库存数量
			tbItem.setNum(99999);
			//是否启用默认
			tbItem.setIsDefault("1");
			//状态 = 正常1
			tbItem.setStatus("1");
			//设置spec
			tbItem.setSpec("{}");
			//设置公共参数
			setItemValues(goods, tbItem);
			//插入数据库
			itemMapper.insert(tbItem);
			
		}
	}

	private void setItemValues(Goods goods, TbItem item) {
		//设置图片 = 商品图片中的第一张
		List<Map> array = JSON.parseArray(goods.getTbGoodsDesc().getItemImages(), Map.class);
		item.setImage((String)array.get(0).get("url"));
		
		//子目录为 商品的第三目录
		item.setCategoryid(goods.getTbGoods().getCategory3Id());
		
		//创建时间
		item.setCreateTime(new Date());
		
		//更新时间
		item.setUpdateTime(new Date());
		
		//商品 ID
		item.setGoodsId(goods.getTbGoods().getId());
		
		//商家编号
		item.setSellerId(goods.getTbGoods().getSellerId());
		
		//分类名称
		TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(item.getCategoryid());
		item.setCategory(itemCat.getName());
		
		//品牌名称
		TbBrand tbBrand = brandMapper.selectByPrimaryKey(goods.getTbGoods().getBrandId());
		item.setBrand(tbBrand.getName());
		
		//商家名称
		TbSeller tbSeller = sellerMapper.selectByPrimaryKey(goods.getTbGoods().getSellerId());
		item.setSeller(tbSeller.getNickName());
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		goodsMapper.updateByPrimaryKey(goods.getTbGoods());
		goodsDescMapper.updateByPrimaryKey(goods.getTbGoodsDesc());
		TbItemExample example = new TbItemExample();
		com.pinyougou.pojo.TbItemExample.Criteria createCriteria = example.createCriteria();
		createCriteria.andGoodsIdEqualTo(goods.getTbGoods().getId());
		List<TbItem> list = itemMapper.selectByExample(example);
		//循环删除修改前的SKU 因为数量上可能已经发生了变化
		for(TbItem item:list) {
			itemMapper.deleteByPrimaryKey(item.getId());
		}
		this.createItemInfo(goods);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		Goods goods = new Goods();
		//获取商品信息
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		goods.setTbGoods(tbGoods);
		//商品明细
		TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(id);
		goods.setTbGoodsDesc(goodsDesc);
		//SKU 详细信息
		TbItemExample example=new TbItemExample();
		com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(id);
		List<TbItem> list = itemMapper.selectByExample(example);
		goods.setItemList(list);
		
		return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//goodsMapper.deleteByPrimaryKey(id);
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			goods.setIsDelete("1");//表示删除
			goodsMapper.updateByPrimaryKey(goods);
			
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		
		if(goods!=null){			
			if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				criteria.andSellerIdLike(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			//只查询未删除的即 == null 的值
			criteria.andIsDeleteIsNull();
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
		/**
		 * 修改商品的审核状态
		 */
		@Override
		public void updateStatus(Long[] ids, String status) {
			for(Long id:ids) {
				TbGoods goods = goodsMapper.selectByPrimaryKey(id);
				goods.setAuditStatus(status);
				goodsMapper.updateByPrimaryKey(goods);
			}
		}
	
}
