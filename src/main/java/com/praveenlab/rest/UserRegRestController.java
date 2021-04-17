package com.praveenlab.rest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.praveenlab.constants.AppConstants;
import com.praveenlab.entity.UserAccountEntity;
import com.praveenlab.props.AppProperties;
import com.praveenlab.service.UserService;

@RestController
public class UserRegRestController {

	@Autowired
	private AppProperties props;

	@Autowired
	private UserService service;

	@GetMapping("/countries")
	public Map<Integer, String> getCountries() {
		Map<Integer, String> countries = service.getCountries();
		return countries;
	}

	@GetMapping("/states/{countryId}")
	public Map<Integer, String> getStates(@PathVariable Integer countryId) {
		Map<Integer, String> states = service.getStates(countryId);
		return states;
	}

	@GetMapping("/cities/{stateId}")
	public Map<Integer, String> getCities(@PathVariable Integer stateId) {
		Map<Integer, String> cities = service.getCities(stateId);
		return cities;
	}

	@GetMapping("/email-check/{email}")
	public String uniqueEmailCheck(@PathVariable String email) {
		UserAccountEntity acc = service.getUserByEmail(email);
		return acc != null ? AppConstants.DUPLICATE : AppConstants.UNIQUE;
	}

	@PostMapping("/saveUser")
	public String saveUser(@RequestBody UserAccountEntity userAcc) {
		UserAccountEntity acc = service.getUserByEmail(userAcc.getUserEmail());
		if (acc == null) {
			Boolean saveUser = service.saveUser(userAcc);
			if (saveUser) {
				return props.getMessages().get(AppConstants.ACC_REG_SUCC);
			}
			return props.getMessages().get(AppConstants.ACC_REG_FAIL);
		} else {
			return props.getMessages().get(AppConstants.USER_ALREADY_REG);
		}
	}
}