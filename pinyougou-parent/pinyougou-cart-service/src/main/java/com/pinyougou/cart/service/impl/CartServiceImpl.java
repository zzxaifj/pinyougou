package com.pinyougou.cart.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
@Service
public class CartServiceImpl implements CartService{

	@Autowired
	private TbItemMapper itemMapper;
	
	@Autowired
	private RedisTemplate<String, ?> redisTemplate;
	
	@Override
	public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
		//1、根据商品SKU ID 查询SKU 商品信息
		TbItem item = itemMapper.selectByPrimaryKey(itemId);
		if(item == null) {
			throw new RuntimeException("商品不存在：ID="+itemId);
		}
		if(!item.getStatus().equals("1")) {
			throw new RuntimeException("商品状态无效");
		}
		//2、获取商家 ID
		String sellerId = item.getSellerId();
		//3、根据商家列表 判断购物车列表中是否存在该商家的购物车数据
		Cart cart = searchCartBySellerId(cartList,sellerId);
		//4、如果购车的列表中不存在该商家的购物车
		if(cart == null) {
			//5、新建购物车
			Cart newCart = new Cart();
			newCart.setSellerId(sellerId);
			newCart.setSellerName(item.getSeller());
			TbOrderItem orderItem = createObjectItem(item,num);
			List<TbOrderItem> orderItems = new ArrayList<>();
			orderItems.add(orderItem);
			newCart.setOrderItemList(orderItems);
			//6、添加至购物车列表中
			cartList.add(newCart);
		}else {
			//7、如果购物车中存在该商家的购物车
			//8、查询购物车明细列表中是否存在该商品
			TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(),itemId);
			//9、如果没有，新增购物车明细	
			if(orderItem == null) {
				TbOrderItem newOrderItem = createObjectItem(item,num);
				cart.getOrderItemList().add(newOrderItem);
			}else {
				//10、如果有，在原购物车明细上添加数量，更改金额
				orderItem.setNum(orderItem.getNum()+num);
				orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));
				//如果数量小于0移除此list
				if(orderItem.getNum() <= 0) {
					cart.getOrderItemList().remove(orderItem);
				}
				if(cart.getOrderItemList().size() == 0) {
					cartList.remove(cart);
				}
			}
		}
		return cartList;
	}
	
	/**
	 * @desc 判断购物车中是否存在 该商家的商品
	 * @auto 创建人：zzx 
	 * @time 时间：2019年5月8日-上午10:23:43 
	 * @param cartList
	 * @param sellerId
	 * @return Cart
	 * @exception 
	 */
	private Cart searchCartBySellerId(List<Cart> cartList,String sellerId) {
		if(cartList.size()<=0) {
			return null;
		}
		for(Cart cart:cartList) {
			if(cart.getSellerId().equals(sellerId)) {
				return cart;
			}
		}
		return null;
	}
	
	/**
	 * @desc 获取 商品明细信息
	 * @auto 创建人：zzx 
	 * @time 时间：2019年5月8日-上午10:36:00 
	 * @param item
	 * @param num
	 * @return TbOrderItem
	 * @exception
	 */
	public TbOrderItem createObjectItem(TbItem item,Integer num) {
		TbOrderItem orderItem = new TbOrderItem();
		if(num <= 0 ) {
			throw  new  RuntimeException("数量非法");
		}
		orderItem.setGoodsId(item.getGoodsId());
		orderItem.setItemId(item.getId());
		orderItem.setNum(num);
		orderItem.setPicPath(item.getImage());
		orderItem.setPrice(item.getPrice());
		orderItem.setSellerId(item.getSellerId());
		orderItem.setTitle(item.getTitle());
		orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
		
		return orderItem;
	}
	
	/**
	 * @desc 查询 商品明细列表中是否存在此商品
	 * @auto 创建人：zzx 
	 * @time 时间：2019年5月8日-上午10:57:53 
	 * @param orderItems
	 * @param itemId
	 * @return TbOrderItem
	 * @exception
	 */
	private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItems,Long itemId) {
		for(TbOrderItem orderItem:orderItems) {
			if(orderItem.getItemId().longValue() == itemId.longValue()) {
				return orderItem;
			}
		}
		return null;
	}
	
	/**
	 * @desc 从redis 缓存中获取购物车列表
	 * @auto 创建人：zzx 
	 * @time 时间：2019年5月10日-上午10:21:04 
	 * @param username
	 * @return List<Cart>
	 * @exception
	 */
	@Override
	public List<Cart> findCartListFromReids(String username) {
		System.out.println("从redis中获取用户购物车列表");
		@SuppressWarnings("unchecked")
		List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
		if(cartList == null) {
			cartList=new ArrayList<>();
		}
		return cartList;
	}

	/**
	 * @desc 添加商品至redis
	 * @auto 创建人：zzx 
	 * @time 时间：2019年5月10日-上午10:22:01 
	 * @param carts void
	 * @exception
	 */
	@Override
	public void addCartListToReids(String username, List<Cart> carts) {
		System.out.println("向redis存入购物车数据....."+username);
		redisTemplate.boundHashOps("cartList").put(username, carts);
	}
	
	/**
	 * @desc 将cookie 中的购物车合并到 redis 中
	 * @auto 创建人：zzx 
	 * @time 时间：2019年5月10日-上午11:22:44 
	 * @param cartList1
	 * @param cartList2
	 * @return List<Cart>
	 * @exception
	 */
	@Override
	public List<Cart> mergeCart(List<Cart> cartList1, List<Cart> cartList2) {
		for(Cart cart:cartList2) {
			List<TbOrderItem> orderItemList = cart.getOrderItemList();
			for(TbOrderItem orderItem:orderItemList) {
				cartList1 = addGoodsToCartList(cartList1, orderItem.getItemId(), orderItem.getNum());
			}
		}
		return cartList1;
	}
}
