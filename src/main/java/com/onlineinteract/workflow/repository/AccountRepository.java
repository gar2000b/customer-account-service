package com.onlineinteract.workflow.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.onlineinteract.workflow.domain.account.v1.AccountEvent;
import com.onlineinteract.workflow.repository.dbclient.DbClient;
import com.onlineinteract.workflow.utility.JsonParser;

@Repository
public class AccountRepository {

	@Autowired
	DbClient dbClient;

	public AccountRepository() {
	}

	public void processEvent(AccountEvent account) {
		if (account.getEventType().toString().equals("AccountCreatedEvent"))
			createAccount(account);
		if (account.getEventType().toString().equals("AccountUpdatedEvent"))
			updateAccount(account);
	}

	private void createAccount(AccountEvent account) {
		MongoDatabase database = dbClient.getMongoClient().getDatabase("customer-accounts");
		Document accountDocument = Document.parse(account.getV1().toString());
		MongoCollection<Document> accountsCollection = database.getCollection("accounts");
		accountsCollection.insertOne(accountDocument);
		System.out.println("Account Persisted to accounts collection");
	}

	private void updateAccount(AccountEvent account) {
		MongoDatabase database = dbClient.getMongoClient().getDatabase("customer-accounts");
		Document accountDocument = Document.parse(account.getV1().toString());
		MongoCollection<Document> accountsCollection = database.getCollection("accounts");
		accountsCollection.replaceOne(new Document("id", account.getV1().getId()), accountDocument);
		System.out.println("Account Updated in accounts collection");
	}

	public AccountEvent getAccount(String accountId) {
		MongoDatabase database = dbClient.getMongoClient().getDatabase("customer-accounts");
		MongoCollection<Document> accountsCollection = database.getCollection("accounts");
		BasicDBObject query = new BasicDBObject();
		query.put("id", accountId);
		FindIterable<Document> accountDocumentsIterable = accountsCollection.find(query);
		for (Document accountDocument : accountDocumentsIterable) {
			System.out.println("Found: " + accountDocument.toJson());
			accountDocument.remove("_id");
			return JsonParser.fromJson(accountDocument.toJson(), AccountEvent.class);
		}

		return null;
	}

	public String getAllAccounts() {
		MongoDatabase database = dbClient.getMongoClient().getDatabase("customer-accounts");
		MongoCollection<Document> accountsCollection = database.getCollection("accounts");
		FindIterable<Document> accountDocumentsIterable = accountsCollection.find();
		List<Document> accountDocuments = new ArrayList<>();
		for (Document accountDocument : accountDocumentsIterable) {
			System.out.println("Removing _id from account");
			accountDocument.remove("_id");
			accountDocuments.add(accountDocument);
		}
		String allAccounts = StreamSupport.stream(accountDocuments.spliterator(), false).map(Document::toJson)
				.collect(Collectors.joining(", ", "[", "]"));

		return allAccounts;
	}
}
