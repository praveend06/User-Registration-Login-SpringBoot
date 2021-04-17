package com.praveenlab.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.praveenlab.domain.LoginRequest;
import com.praveenlab.service.UserService;

@RestController
public class LoginRestController {

	@Autowired
	private UserService service;

	@PostMapping("/login")
	public String login(@RequestBody LoginRequest request) {
		String loginStatus = service.loginCheck(request.getEmail(), request.getPwd());
		return loginStatus;
	}
}
