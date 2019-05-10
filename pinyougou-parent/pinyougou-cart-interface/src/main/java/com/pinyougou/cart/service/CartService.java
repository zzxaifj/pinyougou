package com.pinyougou.cart.service;

import java.util.List;

import com.pinyougou.pojogroup.Cart;

/**
 * 购物车服务接口
 * 创建人:zzx
 * 时间：2019年5月7日-下午3:44:09 
 * @version 1.0.0
 *
 */
public interface CartService {
	/**
	 * @desc 添加购物车
	 * @auto 创建人：zzx 
	 * @time 时间：2019年5月7日-下午4:14:19 
	 * @param cartList 购物车以后的商品
	 * @param itemId 商品 ID 
	 * @param num  商品的个数
	 * @return List<Cart>
	 * @exception
	 */
	public List<Cart> addGoodsToCartList(List<Cart> cartList,Long itemId,Integer num);
	
	/**
	 * @desc 从redis 缓存中获取购物车列表
	 * @auto 创建人：zzx 
	 * @time 时间：2019年5月10日-上午10:21:04 
	 * @param username
	 * @return List<Cart>
	 * @exception
	 */
	public List<Cart> findCartListFromReids(String username);
	
	/**
	 * @desc 添加商品至redis
	 * @auto 创建人：zzx 
	 * @time 时间：2019年5月10日-上午10:22:01 
	 * @param carts void
	 * @exception
	 */
	public void addCartListToReids(String username,List<Cart> carts);
	
	/**
	 * @desc 将cookie 中的购物车合并到 redis 中
	 * @auto 创建人：zzx 
	 * @time 时间：2019年5月10日-上午11:22:44 
	 * @param cartList1
	 * @param cartList2
	 * @return List<Cart>
	 * @exception
	 */
	public List<Cart> mergeCart(List<Cart> cartList1,List<Cart> cartList2);
}
