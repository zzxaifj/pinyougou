package com.pinyougou.cart.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojogroup.Cart;

import entity.Result;
import util.CookieUtil;

@RestController
@RequestMapping("/cart")
public class CartController {
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private HttpServletResponse response;
	
	@Reference(timeout=1000000)
	private CartService cartService;
	
	//cookie中存放数据的key值
	private static String CARTLIST="cartList";
	//编码格式
	private static String ENCODE="UTF-8";
	
	@RequestMapping("/addGoodsToCartList")
	public Result addGoodsToCartList(Long itemId , Integer num) {
		//得到登陆人账号,判断当前是否有人登陆
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		try {
			//获取cookie中已存在的列表
			List<Cart> carts = findCartList();
			//如果用户未登陆，使用的就是默认用户anonymousUser
			if("anonymousUser".equals(username)) {
				//通过后端将商品添加至购物车
				List<Cart> cartList = cartService.addGoodsToCartList(carts, itemId, num);
				//将购物车存入cookie
				CookieUtil.setCookie(request, response, CARTLIST, JSON.toJSONString(cartList), 3600 * 24, ENCODE);
			}else {
				//用户登录后 需要从redis缓存中获取列表
				cartService.addCartListToReids(username, carts);
			}
			return new Result(true, "添加购物车成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "添加购物车失败");
		}
	}
	
	@RequestMapping("/findCartList")
	public List<Cart> findCartList(){
		//得到登陆人账号,判断当前是否有人登陆
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		String cartValues = CookieUtil.getCookieValue(request, CARTLIST,ENCODE);
		if(cartValues == null || "".equals(cartValues)) {
			cartValues = "[]";
		}
		List<Cart> cartList_cookie = JSON.parseArray(cartValues, Cart.class);
		if("anonymousUser".equals(username)) {
			return cartList_cookie;
		}else {
			List<Cart> cartList = cartService.findCartListFromReids(username);//用户登录后，从redis中获取数据信息
			
			//将cookie中的 购物车进行合并
			cartList = cartService.mergeCart(cartList, cartList_cookie);
			//清楚cookie 
			CookieUtil.deleteCookie(request, response, CARTLIST);
			//将购物车存入 reids
			cartService.addCartListToReids(username, cartList);
			return cartList;
		}
	}
}
