package com.hulkhiretech.payments.service;

import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.constant.TransactionStatusEnum;
import com.hulkhiretech.payments.dto.TransactionDTO;
import com.hulkhiretech.payments.service.factory.TransactionStatusFactory;
import com.hulkhiretech.payments.service.interfaces.TransactionStatusHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentStatusService {
	
	private final TransactionStatusFactory statusFactory;
	
	public TransactionDTO updatePayment(TransactionDTO txnDTO) {
		log.info("Updating payment with txnDTO: {}", txnDTO);
		String txnStatus = txnDTO.getTxnStatus();
		
		TransactionStatusEnum statusEnum = TransactionStatusEnum.fromName(txnStatus);
		log.info("Transaction status ID: {}, Enum: {}", txnStatus, statusEnum);
		
		TransactionStatusHandler statusHandler = statusFactory
				.getTransactionStatusHandler(statusEnum);
		
		if (statusHandler == null) {
			log.error("No handler found for transaction status ID: {}", txnStatus);
			throw new RuntimeException(
					"No handler found for transaction status ID: " + txnStatus);
		}
		
		log.info("Handling transaction status with handler: {}", statusHandler);
		txnDTO = statusHandler.handleTransactionStatus(txnDTO);
		return txnDTO;
	}

}
