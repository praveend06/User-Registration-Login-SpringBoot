package com.praveenlab.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.praveenlab.entity.UserAccountEntity;

public interface UserAccountsRepository extends JpaRepository<UserAccountEntity, String> {

	public UserAccountEntity findByUserEmailAndUserPazzword(String email, String pwd);

	public UserAccountEntity findByUserEmail(String emailId);

}
