package com.onlineinteract.workflow.bus;

import java.util.Arrays;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.onlineinteract.workflow.events.AccountCreatedEvent;
import com.onlineinteract.workflow.events.AccountUpdatedEvent;
import com.onlineinteract.workflow.repository.AccountRepository;
import com.onlineinteract.workflow.utility.JsonParser;

@Component
public class AccountConsumer {

	@Autowired
	AccountRepository accountRepository;

	private KafkaConsumer<String, String> consumer;
	private boolean runningFlag = false;

	@PostConstruct
	public void startConsumer() {
		createConsumer();
		processRecords();
	}

	private void createConsumer() {
		Properties buildProperties = buildConsumerProperties();
		consumer = new KafkaConsumer<>(buildProperties);
		consumer.subscribe(Arrays.asList("account-event-topic"));
	}

	private void processRecords() {
		consumer.poll(0);
		consumer.seekToBeginning(consumer.assignment());
		runningFlag = true;
		System.out.println("Spinning up kafka account consumer");
		new Thread(() -> {
			while (runningFlag) {
				ConsumerRecords<String, String> records = consumer.poll(100);
				for (ConsumerRecord<String, String> consumerRecord : records) {
					System.out.println(
							"Consuming event from customer-event-topic with id/key of: " + consumerRecord.key());
					String jsonEvent = consumerRecord.value();
					if (jsonEvent.contains("AccountCreatedEvent")) {
						AccountCreatedEvent event = JsonParser.fromJson(jsonEvent, AccountCreatedEvent.class);
						accountRepository.processEvent(event);
					}
					if (jsonEvent.contains("AccountUpdatedEvent")) {
						AccountUpdatedEvent event = JsonParser.fromJson(jsonEvent, AccountUpdatedEvent.class);
						accountRepository.processEvent(event);
					}
				}
			}
			shutdownConsumerProducer();
			System.out.println("Shutting down kafka account consumer");
		}).start();
	}

	@PreDestroy
	public void shutdownConsumerProducer() {
		System.out.println("*** consumer shutting down");
		consumer.close();
	}

	private Properties buildConsumerProperties() {
		Properties properties = new Properties();
		properties.put("bootstrap.servers", "tiny.canadacentral.cloudapp.azure.com:29092");
		properties.put("group.id", "account-event-topic-group2");
		properties.put("enable.auto.commit", "false");
		properties.put("max.poll.records", "200");
		properties.put("key.deserializer", StringDeserializer.class);
		properties.put("value.deserializer", StringDeserializer.class);
		return properties;
	}
}
