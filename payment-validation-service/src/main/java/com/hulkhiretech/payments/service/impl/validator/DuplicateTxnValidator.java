package com.hulkhiretech.payments.service.impl.validator;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.hulkhiretech.payments.constant.ErrorEnum;
import com.hulkhiretech.payments.constant.MerchantReqUpdate;
import com.hulkhiretech.payments.dao.MerchantPaymentRequestDao;
import com.hulkhiretech.payments.exception.ValidationException;
import com.hulkhiretech.payments.pojo.PaymentRequest;
import com.hulkhiretech.payments.service.interfaces.Validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class DuplicateTxnValidator implements Validator {

	private final MerchantPaymentRequestDao merchantPaymentRequestDao;
	
	private final Gson gson;
	
	@Override
	public void validate(PaymentRequest paymentRequest) {
		log.info("Validating payment request: {}", paymentRequest);
		
		if (paymentRequest.getPayment().getMerchantTxnRef() == null
				|| paymentRequest.getPayment().getMerchantTxnRef().trim().isEmpty()) {
			log.error("Merchant transaction reference is null or empty");
			
			throw new ValidationException(
					ErrorEnum.MERCHANT_TXN_REF_EMPTY.getErrorCode(),
					ErrorEnum.MERCHANT_TXN_REF_EMPTY.getErrorMessage());
		}
		
		paymentRequest.getPayment().setMerchantTxnRef(
				paymentRequest.getPayment().getMerchantTxnRef().trim());
		
		MerchantReqUpdate insertedRows = merchantPaymentRequestDao.insertMerchantPaymentRequest(
				paymentRequest.getUser().getEndUserID(), 
				paymentRequest.getPayment().getMerchantTxnRef(), 
				gson.toJson(paymentRequest));
		
		if (insertedRows == MerchantReqUpdate.DUPLICATE) {//duplicate entry
			log.error("Duplicate entry for merchant payment request");
			
			throw new ValidationException(
					ErrorEnum.DUPLICATE_MERCHANT_TXN_REF.getErrorCode(), 
					ErrorEnum.DUPLICATE_MERCHANT_TXN_REF.getErrorMessage());
			// TODO, add Http Status, and send that as 400 Bad Request
		}
		
		if (insertedRows == MerchantReqUpdate.ERROR) {
			log.error("Error occurred while inserting merchant payment request");
			
			throw new ValidationException(
					ErrorEnum.PAYMENT_NOT_SAVED.getErrorCode(), 
					ErrorEnum.PAYMENT_NOT_SAVED.getErrorMessage());
			// TODO, add Http Status, and send that as 500 Internal Server Error
		}
		
		log.info("DuplicateTxnCheck PASSED SUCCESS merchantTxnRef:{}", 
				paymentRequest.getPayment().getMerchantTxnRef());
	}

}
