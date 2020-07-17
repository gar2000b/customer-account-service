package com.onlineinteract.workflow.domain.account.bus;

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

import com.onlineinteract.workflow.domain.account.AccountEvent;
import com.onlineinteract.workflow.domain.account.repository.AccountRepository;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;

@Component
public class Consumer {

	private static final String ACCOUNT_EVENT_TOPIC = "account-event-topic";

	@Autowired
	AccountRepository accountRepository;

	private KafkaConsumer<String, AccountEvent> consumer;
	private boolean runningFlag = false;

	@PostConstruct
	public void startConsumer() {
		createConsumer();
		processRecords();
	}

	private void createConsumer() {
		Properties buildProperties = buildConsumerProperties();
		consumer = new KafkaConsumer<>(buildProperties);
		consumer.subscribe(Arrays.asList(ACCOUNT_EVENT_TOPIC));
	}

	private void processRecords() {
		consumer.poll(0);
		// consumer.seekToBeginning(consumer.assignment());
		runningFlag = true;
		System.out.println("Spinning up kafka account consumer");
		new Thread(() -> {
			while (runningFlag) {
				ConsumerRecords<String, AccountEvent> records = consumer.poll(100);
				for (ConsumerRecord<String, AccountEvent> consumerRecord : records) {
					System.out.println(
							"Consuming event from customer-event-topic with id/key of: " + consumerRecord.key());
					AccountEvent account = (AccountEvent) consumerRecord.value();
					if (account.getEventType().toString().contains("AccountCreatedEvent"))
						accountRepository.createAccount(account.getV1());
					if (account.getEventType().toString().contains("AccountUpdatedEvent"))
						accountRepository.updateAccount(account.getV1());
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
		properties.put("value.deserializer", KafkaAvroDeserializer.class);
		properties.put("schema.registry.url", "http://tiny.canadacentral.cloudapp.azure.com:8081");
		properties.put("specific.avro.reader", "true");
		return properties;
	}
}
