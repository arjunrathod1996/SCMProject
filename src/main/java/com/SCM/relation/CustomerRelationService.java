package com.SCM.relation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SCM.controllers.Merchant;
import com.SCM.entities.Customer;
import com.SCM.relation.CustomerRelation.Source;

@Service
public class CustomerRelationService {
	
	@Autowired
	CustomerRelationRepository customerRelationRepository;

	public boolean isRelationByTransaction(Customer referred, List<Merchant> relatedMerchants) {
		List<CustomerRelation> relations = customerRelationRepository.findByCustomerAndMerchantIn(referred, relatedMerchants);
		for(CustomerRelation cr : relations) {
			if(cr.getSource() != CustomerRelation.Source.LEAD)
				return true;
		}
		return false;
	}
	
	public void add(Merchant merchant, Customer customer) {
		List<CustomerRelation> relations = customerRelationRepository.findByCustomerAndMerchant(customer, merchant);
		if(relations == null || relations.size() == 0 ) {
			CustomerRelation relation = new CustomerRelation();
			relation.setCustomer(customer);
			relation.setMerchant(merchant);
			relation.setSource(Source.LEAD);
			customerRelationRepository.save(relation);
			
		}
	}
}
