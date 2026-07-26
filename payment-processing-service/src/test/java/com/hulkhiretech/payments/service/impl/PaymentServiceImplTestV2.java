package com.hulkhiretech.payments.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.hulkhiretech.payments.constant.TransactionStatusEnum;
import com.hulkhiretech.payments.dao.interfaces.TransactionDao;
import com.hulkhiretech.payments.dto.TransactionDTO;
import com.hulkhiretech.payments.entity.TransactionEntity;
import com.hulkhiretech.payments.exception.TrustlyProviderException;
import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.http.HttpServiceEngine;
import com.hulkhiretech.payments.pojo.CreateTxnRequest;
import com.hulkhiretech.payments.pojo.CreateTxnResponse;
import com.hulkhiretech.payments.pojo.InitiateTxnRequest;
import com.hulkhiretech.payments.pojo.PaymentResponse;
import com.hulkhiretech.payments.service.PaymentServiceHelper;
import com.hulkhiretech.payments.service.PaymentStatusService;
import com.hulkhiretech.payments.trustlyprovider.TrustlyProviderDepositResponse;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTestV2 {

    @Mock private ModelMapper modelMapper;
    @Mock private PaymentStatusService paymentStatusService;
    @Mock private TransactionDao transactionDao;
    @Mock private PaymentServiceHelper paymentServiceHelper;
    @Mock private HttpServiceEngine httpServiceEngine;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    // === Test 1: createPayment success ===
    @Test
    void testCreatePayment_success() {
        CreateTxnRequest request = new CreateTxnRequest();
        TransactionDTO txnDTO = new TransactionDTO();
        TransactionDTO updatedDTO = new TransactionDTO();
        updatedDTO.setTxnReference("some-txn-ref");
        updatedDTO.setTxnStatus(TransactionStatusEnum.CREATED.getName());

        when(modelMapper.map(request, TransactionDTO.class)).thenReturn(txnDTO);
        when(paymentStatusService.updatePayment(any(TransactionDTO.class))).thenReturn(updatedDTO);

        CreateTxnResponse response = paymentService.createPayment(request);

        assertNotNull(response);
        assertEquals("some-txn-ref", response.getTxnReference());
        assertEquals(TransactionStatusEnum.CREATED.getName(), response.getTxnStatus());

        verify(modelMapper).map(request, TransactionDTO.class);
        verify(paymentStatusService).updatePayment(any(TransactionDTO.class));
    }

    // === Test 2: initiatePayment success ===
    @Test
    void testInitiatePayment_success() {
        String txnReference = UUID.randomUUID().toString();
        InitiateTxnRequest initiateRequest = new InitiateTxnRequest();

        TransactionEntity txnEntity = new TransactionEntity();
        TransactionDTO txnDTO = new TransactionDTO();
        TransactionDTO afterInitiation = new TransactionDTO();
        TransactionDTO afterPending = new TransactionDTO();

        afterInitiation.setTxnReference(txnReference);
        afterInitiation.setTxnStatus(TransactionStatusEnum.INITIATED.getName());

        afterPending.setTxnReference(txnReference);
        afterPending.setTxnStatus(TransactionStatusEnum.PENDING.getName());

        TrustlyProviderDepositResponse providerResponse = new TrustlyProviderDepositResponse();
        providerResponse.setUrl("http://trustly.url");
        providerResponse.setOrderid("order123");

        HttpRequest mockRequest = mock(HttpRequest.class);
        ResponseEntity<String> httpResponse = new ResponseEntity<>("mockBody", HttpStatus.OK);

        when(transactionDao.getTransactionByReference(txnReference)).thenReturn(txnEntity);
        when(modelMapper.map(txnEntity, TransactionDTO.class)).thenReturn(txnDTO);
        when(paymentStatusService.updatePayment(any(TransactionDTO.class))).thenReturn(afterInitiation).thenReturn(afterPending);
        when(paymentServiceHelper.prepareInitiateRequest(any(), any())).thenReturn(mockRequest);
        when(httpServiceEngine.makeHttpCall(mockRequest)).thenReturn(httpResponse);
        when(paymentServiceHelper.processResponse(httpResponse)).thenReturn(providerResponse);

        PaymentResponse response = paymentService.initiatePayment(txnReference, initiateRequest);

        assertNotNull(response);
        assertEquals(txnReference, response.getTxnReference());
        assertEquals("http://trustly.url", response.getUrl());
        assertEquals(TransactionStatusEnum.PENDING.getName(), response.getTxnStatus());

        verify(paymentStatusService, times(2)).updatePayment(any(TransactionDTO.class));
    }

    // === Test 3: initiatePayment throws TrustlyProviderException ===
    @Test
    void testInitiatePayment_trustlyProviderException() {
        String txnReference = UUID.randomUUID().toString();
        InitiateTxnRequest initiateRequest = new InitiateTxnRequest();

        TransactionEntity txnEntity = new TransactionEntity();
        TransactionDTO txnDTO = new TransactionDTO();
        TransactionDTO afterInitiation = new TransactionDTO();

        afterInitiation.setTxnReference(txnReference);
        afterInitiation.setTxnStatus(TransactionStatusEnum.INITIATED.getName());

        HttpRequest mockRequest = mock(HttpRequest.class);

        TrustlyProviderException ex = new TrustlyProviderException(
        		"E101", "Trustly error", HttpStatus.INTERNAL_SERVER_ERROR);

        when(transactionDao.getTransactionByReference(txnReference)).thenReturn(txnEntity);
        when(modelMapper.map(txnEntity, TransactionDTO.class)).thenReturn(txnDTO);
        when(paymentStatusService.updatePayment(any(TransactionDTO.class))).thenReturn(afterInitiation);
        when(paymentServiceHelper.prepareInitiateRequest(any(), any())).thenReturn(mockRequest);
        when(httpServiceEngine.makeHttpCall(mockRequest)).thenThrow(ex);

        TrustlyProviderException thrown = assertThrows(TrustlyProviderException.class, () -> {
            paymentService.initiatePayment(txnReference, initiateRequest);
        });

        assertEquals("E101", thrown.getErrorCode());
        verify(paymentStatusService, times(2)).updatePayment(any(TransactionDTO.class));
    }

    // === Test 4: initiatePayment throws generic Exception ===
    @Test
    void testInitiatePayment_genericException() {
        String txnReference = UUID.randomUUID().toString();
        InitiateTxnRequest initiateRequest = new InitiateTxnRequest();

        TransactionEntity txnEntity = new TransactionEntity();
        TransactionDTO txnDTO = new TransactionDTO();
        TransactionDTO afterInitiation = new TransactionDTO();

        afterInitiation.setTxnReference(txnReference);
        afterInitiation.setTxnStatus(TransactionStatusEnum.INITIATED.getName());

        HttpRequest mockRequest = mock(HttpRequest.class);

        when(transactionDao.getTransactionByReference(txnReference)).thenReturn(txnEntity);
        when(modelMapper.map(txnEntity, TransactionDTO.class)).thenReturn(txnDTO);
        when(paymentStatusService.updatePayment(any(TransactionDTO.class))).thenReturn(afterInitiation);
        when(paymentServiceHelper.prepareInitiateRequest(any(), any())).thenReturn(mockRequest);
        when(httpServiceEngine.makeHttpCall(mockRequest)).thenThrow(new RuntimeException("Something went wrong"));

        Exception thrown = assertThrows(RuntimeException.class, () -> {
            paymentService.initiatePayment(txnReference, initiateRequest);
        });

        assertEquals("Something went wrong", thrown.getMessage());
        verify(paymentStatusService, times(2)).updatePayment(any(TransactionDTO.class));
    }
}
