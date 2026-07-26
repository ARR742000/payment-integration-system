package com.hulkhiretech.payments.dao;

import java.util.List;

public interface ValidationRuleDAO {

	List<String> loadActiveValidatorNames();

}
