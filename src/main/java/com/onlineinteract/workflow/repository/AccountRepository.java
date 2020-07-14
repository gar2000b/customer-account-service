package com.onlineinteract.workflow.repository;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.onlineinteract.workflow.domain.account.Account;
import com.onlineinteract.workflow.events.AccountCreatedEvent;
import com.onlineinteract.workflow.events.AccountUpdatedEvent;
import com.onlineinteract.workflow.events.Event;
import com.onlineinteract.workflow.repository.dbclient.DbClient;
import com.onlineinteract.workflow.utility.JsonParser;

@Repository
public class AccountRepository {

	@Autowired
	DbClient dbClient;

	public AccountRepository() {
	}

	public void processEvent(Event event) {
		if (event instanceof AccountCreatedEvent)
			createAccount(((AccountCreatedEvent) event).getAccount());
		if (event instanceof AccountUpdatedEvent)
			updateAccount(((AccountUpdatedEvent) event).getAccount());
	}

	private void createAccount(Account account) {
		MongoDatabase database = dbClient.getMongoClient().getDatabase("customer-accounts");
		Document accountDocument = Document.parse(JsonParser.toJson(account));
		MongoCollection<Document> accountsCollection = database.getCollection("accounts");
		accountsCollection.insertOne(accountDocument);
		System.out.println("Account Persisted to accounts collection");
	}

	private void updateAccount(Account account) {
		MongoDatabase database = dbClient.getMongoClient().getDatabase("customer-accounts");
		Document accountDocument = Document.parse(JsonParser.toJson(account));
		MongoCollection<Document> accountsCollection = database.getCollection("accounts");
		accountsCollection.replaceOne(new Document("id", account.getId()), accountDocument);
		System.out.println("Account Updated in accounts collection");
	}

	public Account getAccount(String accountId) {
		MongoDatabase database = dbClient.getMongoClient().getDatabase("customer-accounts");
		MongoCollection<Document> accountsCollection = database.getCollection("accounts");
		BasicDBObject query = new BasicDBObject();
		query.put("id", accountId);
		FindIterable<Document> accountDocuments = accountsCollection.find(query);
		for (Document accountDocument : accountDocuments) {
			System.out.println("Found: " + accountDocument.toJson());
			accountDocument.remove("_id");
			return JsonParser.fromJson(accountDocument.toJson(), Account.class);
		}

		return null;
	}

	public String getAllAccounts() {
		MongoDatabase database = dbClient.getMongoClient().getDatabase("customer-accounts");
		MongoCollection<Document> accountsCollection = database.getCollection("accounts");
		FindIterable<Document> accountDocuments = accountsCollection.find();
		for (Document accountDocument : accountDocuments) {
			System.out.println("Removing _id from account");
			accountDocument.remove("_id");
		}
		String allAccounts = StreamSupport.stream(accountDocuments.spliterator(), false).map(Document::toJson)
				.collect(Collectors.joining(", ", "[", "]"));

		return allAccounts;
	}
}
