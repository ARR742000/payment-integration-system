package com.hulkhiretech.payments.service.factory;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.hulkhiretech.payments.constant.TransactionStatusEnum;
import com.hulkhiretech.payments.service.impl.statushandler.CreatedStatusHandler;
import com.hulkhiretech.payments.service.impl.statushandler.FailedStatusHandler;
import com.hulkhiretech.payments.service.impl.statushandler.InitiatedStatusHandler;
import com.hulkhiretech.payments.service.impl.statushandler.PendingStatusHandler;
import com.hulkhiretech.payments.service.interfaces.TransactionStatusHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class TransactionStatusFactory {
	
	private final ApplicationContext applicationContext;
	
	public TransactionStatusHandler getTransactionStatusHandler(
			TransactionStatusEnum txnStatusEnum) {
		log.info("Fetching handler for transaction status txnStatusEnum: {}", txnStatusEnum);
		
		switch (txnStatusEnum) {
		case CREATED:
			return applicationContext.getBean(CreatedStatusHandler.class);
		case INITIATED:
			return applicationContext.getBean(InitiatedStatusHandler.class);
		case PENDING:
			return applicationContext.getBean(PendingStatusHandler.class);
		case FAILED:
			return applicationContext.getBean(FailedStatusHandler.class);
		
		default:
			log.warn("No handler found for transaction status txnStatusEnum: {}", txnStatusEnum);
			return null;
		}
	}

}
