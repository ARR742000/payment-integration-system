package com.hulkhiretech.payments.service.impl.statushandler;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.dao.interfaces.TransactionDao;
import com.hulkhiretech.payments.dto.TransactionDTO;
import com.hulkhiretech.payments.entity.TransactionEntity;
import com.hulkhiretech.payments.service.interfaces.TransactionStatusHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreatedStatusHandler implements TransactionStatusHandler {

	private final TransactionDao transactionDao;
	
	private final ModelMapper modelMapper;
	
	@Override
	public TransactionDTO handleTransactionStatus(TransactionDTO transactionDTO) {
		log.info("Handling transaction status for DTO: {}", transactionDTO);
		
		TransactionEntity entity = modelMapper.map(transactionDTO, 
        		TransactionEntity.class);
		log.info("Mapped TransactionDTO to TransactionEntity: {}", entity);
		
		boolean isCreated = transactionDao.createTransaction(entity);
		log.info("Transaction created: {}", isCreated);
		
		if (!isCreated) {
			log.error("Failed to create transaction in database");
			throw new RuntimeException("Transaction creation failed");
		}
		
		log.info("Transaction successfully created in database, transactionDTO:{}",
				transactionDTO);
		
		return transactionDTO;
	}

}
