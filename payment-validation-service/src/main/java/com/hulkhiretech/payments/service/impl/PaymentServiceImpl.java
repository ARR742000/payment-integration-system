package com.hulkhiretech.payments.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.cache.ValidationRulesCache;
import com.hulkhiretech.payments.constant.ValidatorEnum;
import com.hulkhiretech.payments.pojo.PaymentRequest;
import com.hulkhiretech.payments.pojo.PaymentResponse;
import com.hulkhiretech.payments.service.interfaces.PaymentService;
import com.hulkhiretech.payments.service.interfaces.Validator;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
	
	@Value("${validator.rules}")
    private String validationRules;
	
	private final ApplicationContext applicationContext;
	
	@Value("${mytestkey}")
	private String mytestkey;
	
	private final ValidationRulesCache validationRulesCache;
	
	@Override
	public PaymentResponse createPayment(PaymentRequest paymentDetails) {
		log.info("Received payment details: {}", paymentDetails);
		
		// Split the validation rules and process each validator
		//String[] rules = validationRules.split(",");
		List<String> ruleList = validationRulesCache.getValidationRulesList();
		log.info("Loaded validation rules: {}", ruleList);
		
		for (String rule : ruleList) {
			log.info("Applying validation rule: {}", rule);
			
			Class<? extends Validator> validatorClass = 
					ValidatorEnum.getValidatorClassByName(rule);
			
			Validator validatorBean = null;
			if (validatorClass != null) {
				validatorBean = applicationContext.getBean(validatorClass);
			}
			
			if (validatorBean == null || validatorClass == null) {
				log.warn("Validator not found for rule: {}", rule);
				continue; // Skip if validator not found
			}
			
			log.info("Validator bean retrieved: {}", 
					validatorBean.getClass().getSimpleName());
			validatorBean.validate(paymentDetails);
			
		}
		
		//TODO this is temporary, replace with actual functional values.
		PaymentResponse paymentResponse = new PaymentResponse();
		paymentResponse.setId("12345");
		paymentResponse.setRedirectUrl(
				"https://example.com/redirect?paymentId=" 
		+ paymentResponse.getId());
		
		log.info("Payment response created: {}", paymentResponse);
		return paymentResponse;
	}
	
	@PostConstruct 
	public void loadValidatorRules() {
		validationRulesCache.loadValidatorRulesAndParams();
		log.info("Loaded validator rules from cache");
	}

}