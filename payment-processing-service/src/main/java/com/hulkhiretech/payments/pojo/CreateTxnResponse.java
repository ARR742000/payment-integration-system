package com.hulkhiretech.payments.pojo;

import lombok.Data;

@Data
public class CreateTxnResponse {
	
	private String txnStatus;
	private String txnReference;

}
