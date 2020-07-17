package com.onlineinteract.workflow.domain.customer_account.repository;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.onlineinteract.workflow.dbclient.DbClient;
import com.onlineinteract.workflow.domain.customer_account.CustomerAccount;
import com.onlineinteract.workflow.utility.JsonParser;

@Repository
public class CustomerAccountRepository {

	@Autowired
	DbClient dbClient;

	public CustomerAccountRepository() {
	}

	public void createCustomerAccount(CustomerAccount customerAccount) {
		MongoDatabase database = dbClient.getMongoClient().getDatabase("customer-accounts");
		Document customerAccountDocument = Document.parse(JsonParser.toJson(customerAccount));
		MongoCollection<Document> customerAccountsCollection = database.getCollection("customer-accounts");
		customerAccountsCollection.insertOne(customerAccountDocument);
		System.out.println("Account Persisted to customer-accounts collection");
	}

	public void updateCustomerAccount(CustomerAccount customerAccount) {
		MongoDatabase database = dbClient.getMongoClient().getDatabase("customer-accounts");
		Document customerAccountDocument = Document.parse(JsonParser.toJson(customerAccount));
		MongoCollection<Document> customerAccountsCollection = database.getCollection("customer-accounts");
		customerAccountsCollection.replaceOne(new Document("id", customerAccount.getId()), customerAccountDocument);
		System.out.println("Customer Account Updated in customer-accounts collection");
	}
	
	public CustomerAccount getCustomerAccount(String customerAccountId) {
		MongoDatabase database = dbClient.getMongoClient().getDatabase("customer-accounts");
		MongoCollection<Document> customerAccountsCollection = database.getCollection("customer-accounts");
		BasicDBObject query = new BasicDBObject();
		query.put("id", customerAccountId);
		FindIterable<Document> customerAccountDocuments = customerAccountsCollection.find(query);
		for (Document customerAccountDocument : customerAccountDocuments) {
			System.out.println("Found: " + customerAccountDocument.toJson());
			customerAccountDocument.remove("_id");
			return JsonParser.fromJson(customerAccountDocument.toJson(), CustomerAccount.class);
		}

		return null;
	}

	public List<CustomerAccount> getAllCustomerAccounts() {
		MongoDatabase database = dbClient.getMongoClient().getDatabase("customer-accounts");
		MongoCollection<Document> customerAccountsCollection = database.getCollection("customer-accounts");
		FindIterable<Document> customerAccountDocuments = customerAccountsCollection.find();
		List<CustomerAccount> customerAccounts = new ArrayList<>();
		for (Document customerAccountDocument : customerAccountDocuments) {
			System.out.println("Removing _id from account");
			customerAccountDocument.remove("_id");
			customerAccounts.add(JsonParser.fromJson(customerAccountDocument.toJson(), CustomerAccount.class));
		}

		return customerAccounts;
	}
}
