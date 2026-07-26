package com.hulkhiretech.payments.config;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import com.hulkhiretech.payments.dto.TransactionDTO;
import com.hulkhiretech.payments.entity.TransactionEntity;
import com.hulkhiretech.payments.util.PaymentMethodEnumConverter;
import com.hulkhiretech.payments.util.PaymentTypeEnumConverter;
import com.hulkhiretech.payments.util.ProviderEnumConverter;
import com.hulkhiretech.payments.util.TransactionStatusEnumConverter;
import com.hulkhiretech.payments.util.idtoname.PaymentMethodEnumIdToNameConverter;
import com.hulkhiretech.payments.util.idtoname.PaymentTypeEnumIdToNameConverter;
import com.hulkhiretech.payments.util.idtoname.ProviderEnumIdToNameConverter;
import com.hulkhiretech.payments.util.idtoname.TransactionStatusEnumIdToNameConverter;

@Configuration
public class AppConfig {

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();

		// Use STRICT matching to avoid fuzzy/ambiguous matches
		modelMapper.getConfiguration()
		.setMatchingStrategy(MatchingStrategies.STRICT)
		.setFieldMatchingEnabled(true)
		.setFieldAccessLevel(AccessLevel.PRIVATE);

		Converter<String, Integer> paymentMethodEnumConverter = 
				new PaymentMethodEnumConverter();
		Converter<String, Integer> paymentTypeEnumConverter = 
				new PaymentTypeEnumConverter();
		Converter<String, Integer> providerEnumConverter = 
				new ProviderEnumConverter();
		Converter<String, Integer> transactionStatusEnumConverter = 
				new TransactionStatusEnumConverter();

		modelMapper.addMappings(new PropertyMap<TransactionDTO, TransactionEntity>() {
			@Override
			protected void configure() {
				using(paymentMethodEnumConverter).map(
						source.getPaymentMethod(), destination.getPaymentMethodId());

				using(paymentTypeEnumConverter).map(
						source.getPaymentType(), destination.getPaymentTypeId());

				using(providerEnumConverter).map(
						source.getProvider(), destination.getProviderId());

				using(transactionStatusEnumConverter).map(
						source.getTxnStatus(), destination.getTxnStatusId());
			}
		});

		Converter<Integer, String> paymentMethodEnumIdToStringConverter = new PaymentMethodEnumIdToNameConverter();
		Converter<Integer, String> paymentTypeEnumIdToStringConverter = new PaymentTypeEnumIdToNameConverter();
		Converter<Integer, String> providerEnumIdToStringConverter = new ProviderEnumIdToNameConverter();
		Converter<Integer, String> transactionStatusEnumIdToStringConverter = new TransactionStatusEnumIdToNameConverter();

		modelMapper.addMappings(new PropertyMap<TransactionEntity, TransactionDTO>() {
			@Override 
			protected void configure() {
				using(paymentMethodEnumIdToStringConverter).map(
						source.getPaymentMethodId(), destination.getPaymentMethod());
				using(paymentTypeEnumIdToStringConverter).map(
						source.getPaymentTypeId(), destination.getPaymentType());
				using(providerEnumIdToStringConverter).map(
						source.getProviderId(), destination.getProvider());
				using(transactionStatusEnumIdToStringConverter).map(
						source.getTxnStatusId(), destination.getTxnStatus());
				// Add mappings for txnStatusId and paymentTypeId with their respective converters
			}
		});


		return modelMapper;
	}

	@Bean
	//@LoadBalanced
	public RestClient.Builder loadBalancedRestClientBuilder() {
		return RestClient.builder();
	}
	@Bean
	public RestClient restClient(@Autowired RestClient.Builder builder) {
		return builder.build();
	}

}
