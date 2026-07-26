package com.hulkhiretech.payments.service.impl.validator;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.cache.ValidationRulesCache;
import com.hulkhiretech.payments.constant.ErrorEnum;
import com.hulkhiretech.payments.constant.ValidatorEnum;
import com.hulkhiretech.payments.dao.MerchantPaymentRequestDao;
import com.hulkhiretech.payments.exception.ValidationException;
import com.hulkhiretech.payments.pojo.PaymentRequest;
import com.hulkhiretech.payments.service.interfaces.Validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentAttemptThresholdCheck implements Validator {

	private final MerchantPaymentRequestDao merchantPaymentRequestDao;
	
	private final ValidationRulesCache validationRulesCache;
	
	private static final String DURATION_IN_MINS = "durationInMins";
	private static final String MAX_PAYMENT_THRESHOLD = "maxPaymentThreshold";
	
	@Override
	public void validate(PaymentRequest paymentRequest) {
		log.info("Validating payment request: {}", paymentRequest);
		
		Map<String, String> params = validationRulesCache.getValidationRulesParams(
				ValidatorEnum.PAYMENT_ATTEMPT_THRESHOLD_RULE.name());
		
		int durationInMins = Integer.parseInt(params.get(DURATION_IN_MINS));
		int maxPaymentThreshold = Integer.parseInt(params.get(MAX_PAYMENT_THRESHOLD));
		log.info("Using durationInMins: {} and maxPaymentThreshold: {}", durationInMins, maxPaymentThreshold);
	
		int count = merchantPaymentRequestDao.getUserPaymentAttemptsInLastXMinutes(
				paymentRequest.getUser().getEndUserID(), durationInMins);
		
		log.info("Payment attempts in last {} minutes: {} | endUserId:{}", durationInMins, count, 
                paymentRequest.getUser().getEndUserID());
		
		if (count > maxPaymentThreshold) {
			log.error("Payment attempts exceeded threshold in last {} minutes", durationInMins);
			// throw exception
			
			throw new ValidationException(
					ErrorEnum.PAYMENT_ATTEMPT_THRESHOLD_EXCEEDED.getErrorCode(),
					ErrorEnum.PAYMENT_ATTEMPT_THRESHOLD_EXCEEDED.getErrorMessage());
		}
		
		log.info("PaymentAttemptThresholdCheck passed for endUserId: {}", 
				paymentRequest.getUser().getEndUserID());
	}

}
