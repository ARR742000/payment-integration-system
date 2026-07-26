package com.hulkhiretech.payments.service.interfaces;

import com.hulkhiretech.payments.dto.TransactionDTO;

public interface TransactionStatusHandler {
	
	public TransactionDTO handleTransactionStatus(TransactionDTO transactionDTO);

}
