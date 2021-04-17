package com.praveenlab.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.praveenlab.constants.AppConstants;
import com.praveenlab.domain.UnlockAccount;
import com.praveenlab.entity.CitiesMasterEntity;
import com.praveenlab.entity.CountryMasterEntity;
import com.praveenlab.entity.StateMasterEntity;
import com.praveenlab.entity.UserAccountEntity;
import com.praveenlab.props.AppProperties;
import com.praveenlab.repository.CitiesMasterRepository;
import com.praveenlab.repository.CountryMasterRepository;
import com.praveenlab.repository.StatesMasterRepository;
import com.praveenlab.repository.UserAccountsRepository;
import com.praveenlab.util.EmailUtils;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserAccountsRepository userAccRepo;

	@Autowired
	private CountryMasterRepository countrysRepo;

	@Autowired
	private StatesMasterRepository statesRepo;

	@Autowired
	private CitiesMasterRepository citiesRepo;

	@Autowired
	private EmailUtils emailUtils;

	@Autowired
	private AppProperties props;

	@Override
	public String loginCheck(String email, String pwd) {
		UserAccountEntity entity = userAccRepo.findByUserEmailAndUserPazzword(email, pwd);
		if (entity != null) {
			String accStatus = entity.getAccStatus();
			if (AppConstants.UNLOCKED.equals(accStatus)) {
				return AppConstants.SUCCESS;
			} else {
				return props.getMessages().get(AppConstants.ACC_LOCKED);
			}
		} else {
			return props.getMessages().get(AppConstants.INVALID_CREDENTIALS);
		}
	}

	@Override
	public Map<Integer, String> getCountries() {
		List<CountryMasterEntity> findAll = countrysRepo.findAll();

		Map<Integer, String> countries = new HashMap<>();

		findAll.forEach(entity -> {
			countries.put(entity.getCountryId(), entity.getCountryName());
		});

		return countries;
	}

	@Override
	public Map<Integer, String> getStates(Integer countryId) {
		List<StateMasterEntity> entities = statesRepo.findByCountryId(countryId);
		Map<Integer, String> states = new HashMap<>();
		entities.forEach(entity -> {
			states.put(entity.getStateId(), entity.getStateName());
		});
		return states;
	}

	@Override
	public Map<Integer, String> getCities(Integer stateId) {
		List<CitiesMasterEntity> entities = citiesRepo.findByStateId(stateId);

		Map<Integer, String> cities = new HashMap<>();
		entities.forEach(entity -> {
			cities.put(entity.getCityId(), entity.getCityName());
		});

		return cities;
	}

	@Override
	public UserAccountEntity getUserByEmail(String emailId) {
		UserAccountEntity entity = userAccRepo.findByUserEmail(emailId);
		return entity;
	}

	@Override
	public Boolean saveUser(UserAccountEntity userAcc) {
		String tempPwd = generateRandomPwd(5);
		userAcc.setUserPazzword(tempPwd);
		userAcc.setAccStatus(AppConstants.LOCKED);

		UserAccountEntity save = userAccRepo.save(userAcc);

		if (save.getUserId() != null) {
			Boolean isSent = sendAccRegEmail(userAcc);
			return isSent;
		} else {
			return false;
		}
	}

	@Override
	public String unlockAccount(UnlockAccount acc) {
		UserAccountEntity user = userAccRepo.findByUserEmail(acc.getEmail());
		if (user != null && user.getUserPazzword().equals(acc.getTempPwd())) {
			user.setUserPazzword(acc.getNewPwd());
			user.setAccStatus(AppConstants.UNLOCKED);
			userAccRepo.save(user);
			return "Account Unlocked Successfully";
		}
		return "Incorrect Temporary Password";
	}

	@Override
	public Boolean forgotPassword(String emailId) {
		UserAccountEntity user = userAccRepo.findByUserEmail(emailId);
		if (user != null && AppConstants.UNLOCKED.equals(user.getAccStatus())) {
			return sendPwdToUserEmail(user);
		}
		return false;
	}

	private String generateRandomPwd(Integer length) {
		byte[] array = new byte[length];
		new Random().nextBytes(array);
		String generatedString = new String(array, Charset.forName("UTF-8"));
		return generatedString;
	}

	private Boolean sendAccRegEmail(UserAccountEntity userAcc) {
		String body = getEmailBodyTxt(userAcc, "UNLOCK-ACC-EMAIL-BODY-TEMPLATE.txt");
		return emailUtils.sendEmail("Registration Successfull", body, userAcc.getUserEmail());
	}

	private Boolean sendPwdToUserEmail(UserAccountEntity userAcc) {
		String body = getEmailBodyTxt(userAcc, "RECOVER-PASSWORD-EMAIL-BODY-TEMPLATE.txt");
		emailUtils.sendEmail("Password Recovery", body, userAcc.getUserEmail());
		return true;
	}

	private String getEmailBodyTxt(UserAccountEntity userAcc, String fileName) {
		String mailBody = null;
		StringBuilder sb = new StringBuilder();
		try {
			FileReader fr = new FileReader(fileName);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
			br.close();
			mailBody = sb.toString();
			mailBody = mailBody.replace("{FNAME}", userAcc.getFirstName());
			mailBody = mailBody.replace("{LNAME}", userAcc.getUserLastName());
			mailBody = mailBody.replace("{TEMP-PWD}", userAcc.getUserPazzword());
			mailBody = mailBody.replace("{EMAIL}", userAcc.getUserEmail());
			mailBody = mailBody.replace("{PWD}", userAcc.getUserPazzword());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mailBody;
	}
}
