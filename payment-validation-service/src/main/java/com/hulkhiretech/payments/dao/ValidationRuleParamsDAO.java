package com.hulkhiretech.payments.dao;

import java.util.Map;

public interface ValidationRuleParamsDAO {

	Map<String, String> loadValidatorRuleParams(String validatorRuleName);
}
