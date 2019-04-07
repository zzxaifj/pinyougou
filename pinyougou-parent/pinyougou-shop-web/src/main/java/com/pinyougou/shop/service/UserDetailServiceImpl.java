package com.pinyougou.shop.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.service.SellerService;

/**
 * @author：创建人:Administrator
 * @time 时间：2019年4月6日-上午11:36:51 
 * @version 1.0.0
 * @desc 安全框架逻辑实现层
 */
public class UserDetailServiceImpl implements UserDetailsService{

	private SellerService sellerService;
	
	public void setSellerService(SellerService sellerService) {
		this.sellerService = sellerService;
	}



	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		TbSeller tbSeller = sellerService.findOne(username);
		//1:表示审核通过
		if(tbSeller!=null&&tbSeller.getStatus().equals("1")) {
			String password = tbSeller.getPassword();
			
			List<GrantedAuthority> authorities=new ArrayList<>();
			//角色目前没有在数据库中维护,统一为 ROLE_SELLER
			authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
			User user = new User(username, password, authorities);
			return user;
		}else {
			return null;
		}
	}

}
