package com.onlineinteract.workflow.rest;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.onlineinteract.workflow.domain.account.repository.AccountRepository;
import com.onlineinteract.workflow.domain.customer.repository.AccountCustomerRepository;
import com.onlineinteract.workflow.domain.customer_account.CustomerAccount;
import com.onlineinteract.workflow.domain.customer_account.repository.CustomerAccountRepository;
import com.onlineinteract.workflow.utility.JsonParser;

@Controller
@EnableAutoConfiguration
public class CustomerAccountController {

	@Autowired
	CustomerAccountRepository customerAccountRepository;

	@Autowired
	AccountCustomerRepository accountCustomerRepository;

	@Autowired
	AccountRepository accountRepository;

	@RequestMapping(method = RequestMethod.POST, consumes = "application/json", produces = "application/json", value = "/customer-account/account/{accountId}/customer/{customerId}")
	@ResponseBody
	public ResponseEntity<String> createCustomerAccount(@PathVariable String accountId,
			@PathVariable String customerId) {
		System.out.println("*** createCustomerAccount() called ***");
		String customerAccountId = UUID.randomUUID().toString();
		CustomerAccount customerAccount = new CustomerAccount();
		customerAccount.setId(customerAccountId);
		customerAccount.setAccountId(accountId);
		customerAccount.setCustomerId(customerId);
		customerAccountRepository.createCustomerAccount(customerAccount);
		
		customerAccount.setAccount(accountRepository.getAccount(accountId));
		customerAccount.setCustomer(accountCustomerRepository.getCustomer(customerId));
		return new ResponseEntity<>("createCustomerAccount(): " + JsonParser.toJson(customerAccount), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.PUT, consumes = "application/json", produces = "application/json", value = "/customer-account/{customerAccountId}/account/{accountId}/customer/{customerId}")
	@ResponseBody
	public ResponseEntity<String> updateCustomerAccount(@PathVariable String customerAccountId,
			@PathVariable String accountId, @PathVariable String customerId) {
		System.out.println("*** updateCustomerAccount() called ***");
		CustomerAccount customerAccount = new CustomerAccount();
		customerAccount.setId(customerAccountId);
		customerAccount.setAccountId(accountId);
		customerAccount.setCustomerId(customerId);
		customerAccountRepository.updateCustomerAccount(customerAccount);
		
		customerAccount.setAccount(accountRepository.getAccount(accountId));
		customerAccount.setCustomer(accountCustomerRepository.getCustomer(customerId));
		return new ResponseEntity<>("updateCustomerAccount(): " + JsonParser.toJson(customerAccount), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, produces = "application/json", value = "/customer-account/{customerAccountId}")
	@ResponseBody
	public ResponseEntity<String> getCustomerAccount(@PathVariable String customerAccountId) {
		System.out.println("*** getCustomerAccount() called with accountId of: " + customerAccountId + " ***");
		CustomerAccount customerAccount = customerAccountRepository.getCustomerAccount(customerAccountId);
		customerAccount.setAccount(accountRepository.getAccount(customerAccount.getAccountId()));
		customerAccount.setCustomer(accountCustomerRepository.getCustomer(customerAccount.getCustomerId()));
		return new ResponseEntity<>("getCustomerAccount(): " + JsonParser.toJson(customerAccount), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, produces = "application/json", value = "/customer-accounts")
	@ResponseBody
	public ResponseEntity<String> getAllCustomerAccounts() {
		System.out.println("*** getAllCustomerAccounts() called ***");
		List<CustomerAccount> allCustomerAccounts = customerAccountRepository.getAllCustomerAccounts();
		for (CustomerAccount customerAccount : allCustomerAccounts) {
			customerAccount.setAccount(accountRepository.getAccount(customerAccount.getAccountId()));
			customerAccount.setCustomer(accountCustomerRepository.getCustomer(customerAccount.getCustomerId()));
		}

		return new ResponseEntity<>("getAllCustomerAccounts(): " + JsonParser.toJson(allCustomerAccounts),
				HttpStatus.OK);
	}

}
