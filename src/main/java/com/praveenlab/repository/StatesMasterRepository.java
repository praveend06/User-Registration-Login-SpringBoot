package com.praveenlab.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.praveenlab.entity.StateMasterEntity;

public interface StatesMasterRepository extends JpaRepository<StateMasterEntity, Integer> {

	public List<StateMasterEntity> findByCountryId(Integer countryId);

}
