package com.pinyougou.page.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbItemExample.Criteria;

import freemarker.template.Configuration;
import freemarker.template.Template;
@Service
public class ItemPageServiceImpl implements ItemPageService{
	
	@Value("${pagedir}")
	private String pagedir;
	
	@Autowired
	private FreeMarkerConfigurer freemarkerConfig;
	
	@Autowired
	private TbGoodsMapper goodsMapper;
	
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	
	@Autowired
	private TbItemMapper itemMapper;
	
	@Autowired
	private TbItemCatMapper itemCatMapper;
	
	
	@Override
	public boolean genItemHtml(Long goodsId) {
		//获取配置类
		try {
			Configuration configuration = freemarkerConfig.getConfiguration();
			//获取模板对象
			Template template = configuration.getTemplate("item.ftl");
			
			//获取数据对象
			Map<String, Object> map = new HashMap<>();
			//1、获取商品信息
			TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
			map.put("goods", goods);
			//2、获取商品 描述信息
			TbGoodsDesc goodsDescs = goodsDescMapper.selectByPrimaryKey(goodsId);
			map.put("goodsDescs", goodsDescs);
			//3、获取商品信息
			TbItemExample example=new TbItemExample();
			Criteria criteria = example.createCriteria();
			criteria.andGoodsIdEqualTo(goodsId);
			criteria.andStatusEqualTo("1");//只获取审核通过的产品
			example.setOrderByClause("is_default desc");
			List<TbItem> itemList = itemMapper.selectByExample(example);
			map.put("itemList", itemList);
			//4、组装分类数据
			String category1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
			map.put("category1", category1);
			String category2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
			map.put("category2", category2);
			String category3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();
			map.put("category3", category3);
			
			//文件输出路径
			Writer out = new FileWriter(pagedir+goodsId+".html");
			//模板 数据  输出路径 整合
			template.process(map, out );
			//关闭文件流
			out.close();
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return false;
	}


	@Override
	public boolean delItemHtml(Long[] ids) {
		//进行删除
		try {
			List<Long> goodsId = Arrays.asList(ids);
			for(Long id : goodsId) {
				//获取文件进行删除
				File file = new File(pagedir+id+".html");
				if(file != null) {
					file.delete();
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
