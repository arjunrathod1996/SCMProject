package com.SCM.referral;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.SCM.controllers.Merchant;
import com.SCM.entities.Customer;

@Repository
public interface ReferralRepository extends CrudRepository<Refferal, Long>{

	List<Refferal> findByMerchantInAndReferred(List<Merchant> relatedMerchants, Customer referred);

}
