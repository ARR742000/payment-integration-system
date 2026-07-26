package com.hulkhiretech.payments.dao.impl;

import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.hulkhiretech.payments.dao.ValidationRuleParamsDAO;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class ValidationRuleParamsDAOImpl implements ValidationRuleParamsDAO {

	private NamedParameterJdbcTemplate jdbcTemplate;

	public ValidationRuleParamsDAOImpl(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public Map<String, String> loadValidatorRuleParams(String validatorName) {
		String sql = "SELECT paramName, paramValue FROM validation_rules_params WHERE validatorName = :validatorName";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("validatorName", validatorName);

        return jdbcTemplate.query(sql, params, rs -> {
            Map<String, String> resultMap = new java.util.HashMap<>();
            while (rs.next()) {
                resultMap.put(rs.getString("paramName"), rs.getString("paramValue"));
            }
            return resultMap;
        }); 
	}

}
