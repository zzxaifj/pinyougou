package com.yintong.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yintong.service.IUserService;

@Controller
@RequestMapping("/User")
public class UserController {
	
	@Reference
	private IUserService uService;
	
	@RequestMapping("/getName")
	@ResponseBody
	public String getName() {
		return uService.getName();
	}
}
