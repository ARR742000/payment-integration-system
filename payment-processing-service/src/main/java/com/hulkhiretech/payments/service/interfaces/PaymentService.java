package com.hulkhiretech.payments.service.interfaces;

import com.hulkhiretech.payments.pojo.CreateTxnRequest;
import com.hulkhiretech.payments.pojo.CreateTxnResponse;
import com.hulkhiretech.payments.pojo.InitiateTxnRequest;
import com.hulkhiretech.payments.pojo.PaymentResponse;

public interface PaymentService {
	
	public CreateTxnResponse createPayment(CreateTxnRequest createTxnRequest);
	
	public PaymentResponse initiatePayment(
			String txnReference, InitiateTxnRequest initiateTxnRequest);

}
