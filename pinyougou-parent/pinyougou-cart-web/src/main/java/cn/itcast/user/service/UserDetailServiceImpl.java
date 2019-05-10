package cn.itcast.user.service;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailServiceImpl implements UserDetailsService{

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		GrantedAuthority role_user = new SimpleGrantedAuthority("ROLE_USER");
		authorities.add(role_user);
		//主要用于返回用户的角色，因为用户密码通过cas进行了校验，不需要通过security进行处理
		User user = new User(username, "", authorities);
		return user;
	}

}
