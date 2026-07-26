package com.hulkhiretech.payments.dao.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.hulkhiretech.payments.constant.MerchantReqUpdate;
import com.hulkhiretech.payments.dao.MerchantPaymentRequestDao;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class MerchantPaymentRequestDaoImpl implements MerchantPaymentRequestDao {

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public MerchantPaymentRequestDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

	@Override
	public MerchantReqUpdate insertMerchantPaymentRequest(String endUserID, String merchantTransactionReference,
			String transactionRequest) {
		log.debug(
				"Inserting merchant payment request in DB endUserId:{}"
						+ "|merchantTransactionReference:{}"
						+ "|transactionRequest:{}",
						endUserID, merchantTransactionReference, transactionRequest);

		String sql = "INSERT INTO merchant_payment_request " +
				"(endUserID, merchantTransactionReference, transactionRequest) " +
				"VALUES (:endUserID, :merchantTransactionReference, :transactionRequest)";

		log.info("Inserting merchant payment request in DB: {}", sql);

		MapSqlParameterSource params = new MapSqlParameterSource()
				.addValue("endUserID", endUserID)
				.addValue("merchantTransactionReference", merchantTransactionReference)
				.addValue("transactionRequest", transactionRequest);

		try {
			int insertedRow = namedParameterJdbcTemplate.update(sql, params);

			log.info("Merchant payment request inserted in DB. Rows inserted: {}", insertedRow);
			return (insertedRow == 1 ? MerchantReqUpdate.SAVED : MerchantReqUpdate.ERROR);
		} catch (DuplicateKeyException e) {
			log.error("DuplicateKeyException Error occurred while inserting merchant payment request in DB", 
					e.getMessage(), e);
			return MerchantReqUpdate.DUPLICATE;
		} catch (Exception e) {
			log.error("Exception Error occurred while inserting merchant payment request in DB", 
					e.getMessage(), e);
			return MerchantReqUpdate.ERROR;
		}
	}

	@Override
	public int getUserPaymentAttemptsInLastXMinutes(String endUserId, int durationInMins) {
		String sql = "SELECT COUNT(*) FROM validations.merchant_payment_request " +
				"WHERE endUserID = :endUserId " +
				"AND creationDate BETWEEN :startTime AND :currentTime";

		// Calculate the start time
		LocalDateTime currentTime = LocalDateTime.now();
		LocalDateTime startTime = currentTime.minusMinutes(durationInMins);
		//TODO endtime: currentTime + 30s, or 1 min. startTime - endtime. 
		
		Map<String, Object> params = new HashMap<>();
		params.put("endUserId", endUserId);
		params.put("startTime", startTime);
		params.put("currentTime", currentTime);

		Integer paymentAttemptCount = namedParameterJdbcTemplate.queryForObject(
				sql, params, Integer.class);
		
		log.info("Payment attempt count for user||endUserId:{}|durationInMins:{}|paymentAttemptCount:{}", 
				endUserId, durationInMins, paymentAttemptCount);
		
		return paymentAttemptCount;
	}

}
