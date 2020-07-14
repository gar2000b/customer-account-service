package com.onlineinteract.workflow.repository;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.onlineinteract.workflow.domain.customer.read.AccountCustomer;
import com.onlineinteract.workflow.domain.customer.write.Customer;
import com.onlineinteract.workflow.events.CustomerCreatedEvent;
import com.onlineinteract.workflow.events.CustomerUpdatedEvent;
import com.onlineinteract.workflow.events.Event;
import com.onlineinteract.workflow.repository.dbclient.DbClient;
import com.onlineinteract.workflow.utility.JsonParser;

@Repository
public class AccountCustomerRepository {

	@Autowired
	DbClient dbClient;

	public AccountCustomerRepository() {
	}

	public void processEvent(Event event) {
		if (event instanceof CustomerCreatedEvent)
			createCustomer(((CustomerCreatedEvent) event).getCustomer());
		if (event instanceof CustomerUpdatedEvent)
			updateCustomer(((CustomerUpdatedEvent) event).getCustomer());

	}

	private void createCustomer(Customer customer) {
		AccountCustomer accountCustomer = new AccountCustomer();
		accountCustomer.setId(customer.getId());
		accountCustomer.setForename(customer.getForename());
		accountCustomer.setSurname(customer.getSurname());
		accountCustomer.setSin(customer.getSin());
		accountCustomer.setDob(customer.getDob());
		accountCustomer.setPostcode(customer.getPostcode());

		MongoDatabase database = dbClient.getMongoClient().getDatabase("customer-accounts");
		Document accountCustomerDocument = Document.parse(JsonParser.toJson(accountCustomer));
		MongoCollection<Document> accountsCollection = database.getCollection("customers");
		accountsCollection.insertOne(accountCustomerDocument);
		System.out.println("Account Customer Persisted to account-customers collection");
	}

	private void updateCustomer(Customer customer) {
		AccountCustomer accountCustomer = new AccountCustomer();
		accountCustomer.setId(customer.getId());
		accountCustomer.setForename(customer.getForename());
		accountCustomer.setSurname(customer.getSurname());
		accountCustomer.setSin(customer.getSin());
		accountCustomer.setDob(customer.getDob());
		accountCustomer.setPostcode(customer.getPostcode());

		MongoDatabase database = dbClient.getMongoClient().getDatabase("customer-accounts");
		Document accountCustomerDocument = Document.parse(JsonParser.toJson(accountCustomer));
		MongoCollection<Document> accountsCollection = database.getCollection("customers");
		accountsCollection.replaceOne(new Document("id", accountCustomer.getId()), accountCustomerDocument);
		System.out.println("Account Customer Updated in account-customers collection");
	}

	public AccountCustomer getCustomer(String customerId) {
		MongoDatabase database = dbClient.getMongoClient().getDatabase("customer-accounts");
		MongoCollection<Document> accountCustomerCollection = database.getCollection("customers");
		BasicDBObject query = new BasicDBObject();
		query.put("id", customerId);
		FindIterable<Document> accountCustomerDocuments = accountCustomerCollection.find(query);
		for (Document accountCustomerDocument : accountCustomerDocuments) {
			System.out.println("Found: " + accountCustomerDocument.toJson());
			accountCustomerDocument.remove("_id");
			return JsonParser.fromJson(accountCustomerDocument.toJson(), AccountCustomer.class);
		}

		return null;
	}
}
