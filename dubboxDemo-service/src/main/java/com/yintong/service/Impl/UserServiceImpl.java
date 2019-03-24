package com.yintong.service.Impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.yintong.service.IUserService;

@Service
public class UserServiceImpl implements IUserService{

	@Override
	public String getName() {
		return "Dubbox";
	}

}
