package com.SCM.referral;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SCM.controllers.Merchant;
import com.SCM.entities.Business;
import com.SCM.entities.Customer;
import com.SCM.entities.User;
import com.SCM.referral.Refferal.Source;
import com.SCM.relation.CustomerRelationRepository;
import com.SCM.relation.CustomerRelationService;
import com.SCM.repository.BusinessRepository;
import com.SCM.service.CommonService;

@Service
public class ReferralService {
	
	@Autowired
	ReferralRepository refferalRepositoty;
	
	@Autowired
	BusinessRepository businessRepository;
	
	@Autowired
	CustomerRelationRepository customerRelationRepository;
	
	@Autowired
	CommonService commonService;
	
	@Autowired
	CustomerRelationService customerRelationService;
	
	private static final Logger logger = LoggerFactory.getLogger(ReferralService.class);
	

	
	public void create(User user, Refferal referral) {
        create(user, referral, true);
    }

    public void create(User user, Refferal referral, boolean isNotifyReferrer) {
        try {
            Customer customer = user.getCustomer();
            Merchant merchant = user.getMerchant();

            if (customer != null) {
                merchant = referral.getMerchant();

                if (referral.getReferrer() == null) {
                    referral.setSource(Source.MERCHANT_DIRECT);
                } else {
                    if (referral.getReferrer().equals(customer)) {
                        referral.setSource(Source.CUSTOMER_DIRECT);
                    } else {
                        referral.setSource(Source.CUSTOMER_INVITE);
                    }
                }

            } else if (merchant != null) {
                Business business = merchant.getBusiness();
                if (referral.getReferrer() == null) {
                    throw new RuntimeException("Not Allowed, without Referrer");
                }

                if (referral.getMerchant() == null) {
                    referral.setMerchant(merchant);
                }
                referral.setSource(Source.MERCHANT_DIRECT);
            } else {
                throw new RuntimeException("Not Allowed");
            }

            validate(referral);
            referral = refferalRepositoty.save(referral);

            logger.info("Referral created successfully: {}", referral);

        } catch (RuntimeException e) {
            logger.error("Error creating referral: {}", referral, e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error creating referral: {}", referral, e);
            throw new RuntimeException("Unexpected error creating referral", e);
        }
    }

    private void validate(Refferal referral) {
        try {
            Merchant merchant = referral.getMerchant();
            Business business = merchant.getBusiness();
            Customer referrer = referral.getReferrer();
            Customer referred = referral.getReferred();

            if (referrer != null && referrer.equals(referred)) {
                throw new RuntimeException("Not Allowed, Are You Kidding, you can't refer yourself");
            }

            List<Merchant> relatedMerchants = commonService.getMerchants(business);

            if (customerRelationService.isRelationByTransaction(referred, relatedMerchants)) {
                throw new RuntimeException("Not Allowed, " + referred.getFirstName() + " is an existing Customer.");
            }

            List<Refferal> referrals = refferalRepositoty.findByMerchantInAndReferred(relatedMerchants, referred);
            if (referrals != null && !referrals.isEmpty()) {
                throw new RuntimeException("Not Allowed, Already referred");
            }

            logger.info("Referral validated successfully: {}", referral);

        } catch (RuntimeException e) {
            logger.error("Validation error for referral: {}", referral, e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during referral validation: {}", referral, e);
            throw new RuntimeException("Unexpected error during referral validation", e);
        }
    }

}
