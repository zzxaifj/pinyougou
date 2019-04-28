package com.pinyougou.manager.controller;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.service.GoodsService;

import entity.PageResult;
import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference(timeout=100000)
	private GoodsService goodsService;
	
	//@Reference(timeout=100000)
	//private ItemSearchService itemSearchService;
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	@Autowired
	private javax.jms.Destination queueTextDestination;
	
	@Autowired
	private javax.jms.Destination queueTextDeleteDestination;
	
	@Autowired
	private javax.jms.Destination topicPageDestination;
	
	@Autowired
	private javax.jms.Destination topicPageDeleteDestination;
	
	//@Reference(timeout=100000)
	//private ItemPageService itemPageService;
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		try {
			String name = SecurityContextHolder.getContext().getAuthentication().getName();
			//商家的 ID
			goods.getTbGoods().setSellerId(name);
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@Transactional
	@RequestMapping("/delete")
	public Result delete(final Long [] ids){
		try {
			goodsService.delete(ids);
			//从 solr 中删除 相关SPU 的 SKU 商品
			//itemSearchService.deleteByGoodsIds(ids);
			jmsTemplate.send(queueTextDeleteDestination,new MessageCreator() {
				
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});
			//传入静态页面生成模块的队列
			jmsTemplate.send(topicPageDeleteDestination,new MessageCreator() {
				
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});
			//发送给模板引擎
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param brand
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		return goodsService.findPage(goods, page, rows);		
	}
	
	/**
	 * @desc 修改商品 审核状态
	 * @auto 创建人：zzx 
	 * @time 时间：2019年4月8日-下午3:16:15 
	 * @param ids
	 * @param status
	 * @return Result
	 * @exception
	 */
	@RequestMapping("/updateStatus")
	public Result updateStatus(Long[] ids,String status) {
		try {
			goodsService.updateStatus(ids, status);
			//将审核通过的交易  导入的 solr 中
			if("1".equals(status)) {//审核通过
				List<TbItem> items = goodsService.findItemByGoodsId(ids, status);
				if(items.size()>0) {
					//与搜索服务进行解耦
					//itemSearchService.importList(items);
					
					//发送到 JMS 的消息
					final String jsonString = JSON.toJSONString(items);
					//调用jms 模板发送消息 ,需要出入发送目标的队列
					jmsTemplate.send(queueTextDestination,new MessageCreator() {
						
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(jsonString);
						}
					});
				}else {
					System.out.println("没有明细数据");
				}
				//生成 商品购买页面 模板ftl——>html
				for(final Long goodsId:ids) {
					//itemPageService.genItemHtml(goodsId);
					//通过消息中间件实现 静态模板引擎 实现管理模块与静态模板引擎模块之间实现0耦合
					jmsTemplate.send(topicPageDestination,new MessageCreator() {
						
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(goodsId+"");
						}
					});
				}
			}
			
			
			return new Result(true, "审核成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "审核失败");
		}
	}
	
	@RequestMapping("/gene")
	public void gene(Long id) {
		//itemPageService.genItemHtml(id);
	}
}
