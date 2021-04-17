package com.praveenlab.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.praveenlab.constants.AppConstants;
import com.praveenlab.props.AppProperties;
import com.praveenlab.service.UserService;

@RestController
public class ForgotPasswordRestController {

	@Autowired
	private UserService service;
	
	@Autowired
	private AppProperties props;

	@GetMapping("/forgotPwd/{email}")
	public String forgotPassword(@PathVariable String email) {
		Boolean forgotPassword = service.forgotPassword(email);
		if (forgotPassword) {
			return props.getMessages().get(AppConstants.PWD_RECOVER_SUCC);
		} else {
			return props.getMessages().get(AppConstants.PWD_RECOVER_FAIL);
		}
	}
}
