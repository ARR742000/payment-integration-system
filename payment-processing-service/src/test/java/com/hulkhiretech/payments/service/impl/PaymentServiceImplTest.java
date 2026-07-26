package com.hulkhiretech.payments.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.hulkhiretech.payments.dao.interfaces.TransactionDao;
import com.hulkhiretech.payments.dto.TransactionDTO;
import com.hulkhiretech.payments.http.HttpServiceEngine;
import com.hulkhiretech.payments.pojo.CreateTxnRequest;
import com.hulkhiretech.payments.pojo.CreateTxnResponse;
import com.hulkhiretech.payments.service.PaymentServiceHelper;
import com.hulkhiretech.payments.service.PaymentStatusService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplTest {
	
	@Mock
	private ModelMapper modelMapper;
	
	@Mock
	private PaymentStatusService paymentStatusService;

	@Mock
	private TransactionDao transactionDao;

	@Mock
	private PaymentServiceHelper paymentServiceHelper;

	@Mock
	private HttpServiceEngine httpServiceEngine;

	// Class to test
	@InjectMocks
	private PaymentServiceImpl paymentServiceImpl;

	@Test
	public void myTestMethod() {
		log.info("Executing myTestMethod in PaymentServiceImplTest");
		// Part1	Arrange data
		CreateTxnRequest createTxnRequest = new CreateTxnRequest();
		
		TransactionDTO transactionDTO = new TransactionDTO();
		transactionDTO.setCurrency("EUR");
		
		// set mock behaviour for modelMapper.map(createTxnRequest, TransactionDTO.class); return transactionDTO object
		when(modelMapper.map(createTxnRequest, TransactionDTO.class)
				).thenReturn(transactionDTO);
		
		// overrite behaviour using when -then for paymentStatusService.updatePayment(txnDTO);
		when(paymentStatusService.updatePayment(transactionDTO)
				).thenReturn(transactionDTO);
		
		// Part2 Act on the method to test
		CreateTxnResponse response = paymentServiceImpl.createPayment(createTxnRequest);
		log.info("Response from createPayment: {}", response);
		
		// Part3	Verify the results
		assertNotNull(response);
		assertEquals("CREATED", response.getTxnStatus());
		assertNotNull(response.getTxnReference());
		assertTrue(response.getTxnReference().length() == 36);//Always a UUID
		
		assertEquals("CREATED", transactionDTO.getTxnStatus());
	}

	//@Test
	public void testInitiateMethod() {
		log.info("Executing testInitiateMethod in PaymentServiceImplTest");
		//paymentServiceImpl.initiatePayment(null, null);

	}
}
