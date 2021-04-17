package com.praveenlab.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.praveenlab.entity.CountryMasterEntity;

public interface CountryMasterRepository extends JpaRepository<CountryMasterEntity, Integer> {

}
