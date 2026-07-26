package com.hulkhiretech.payments.dao.interfaces;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.hulkhiretech.payments.entity.TransactionEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
@RequiredArgsConstructor
public class TransactionDaoImpl implements TransactionDao {

	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Override
	public boolean createTransaction(TransactionEntity entity) {
		log.info("Creating transaction in DAO layer entity:{}", entity);

		String sql = "INSERT INTO payments.Transaction (" +
				"userId, paymentMethodId, providerId, paymentTypeId, txnStatusId, " +
				"amount, currency, merchantTransactionReference, txnReference, providerReference, " +
				"errorCode, errorMessage, retryCount) " +
				"VALUES (:userId, :paymentMethodId, :providerId, :paymentTypeId, :txnStatusId, " +
				":amount, :currency, :merchantTransactionReference, :txnReference, :providerReference, " +
				":errorCode, :errorMessage, :retryCount)";

		BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(entity);

		int rowsAffected = namedParameterJdbcTemplate.update(sql, paramSource);
		log.info("Rows affected by transaction creation: {}", rowsAffected);
		return rowsAffected == 1;
	}

	@Override
	public TransactionEntity getTransactionByReference(String txnReference) {
		String sql = "SELECT * FROM payments.Transaction WHERE txnReference = :txnReference";

		Map<String, Object> params = new HashMap<>();
		params.put("txnReference", txnReference);

		TransactionEntity entity = namedParameterJdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(TransactionEntity.class));
		log.info("Transaction retrieved successfully for reference: {}", txnReference);
		return entity;
	}

	@Override
	public boolean updateTransaction(TransactionEntity entity) {
		log.info("Updating transaction in DAO layer for txnReference:{} | txnStatusId:{} | providerReference:{} | errorCode:{} | errorMessage:{}",
				entity.getTxnReference(), entity.getTxnStatusId(), entity.getProviderReference(), entity.getErrorCode(), entity.getErrorMessage());

		
		String sql = "UPDATE payments.Transaction " +
				"SET txnStatusId = :txnStatusId, " +
				"providerReference = :providerReference, " +
				"errorCode = :errorCode, " +
				"errorMessage = :errorMessage " +
				"WHERE txnReference = :txnReference";

		Map<String, Object> params = new HashMap<>();
		params.put("txnStatusId", entity.getTxnStatusId());
		params.put("providerReference", entity.getProviderReference());
		params.put("errorCode", entity.getErrorCode());
		params.put("errorMessage", entity.getErrorMessage());
		params.put("txnReference", entity.getTxnReference());

		int updated = namedParameterJdbcTemplate.update(sql, params);
		log.info("Transaction updated successfully for reference: {}, rows affected: {}", entity.getTxnReference(), updated);
		return updated > 0;
	}

}
