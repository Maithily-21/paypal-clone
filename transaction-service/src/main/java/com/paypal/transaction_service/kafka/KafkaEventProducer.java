package com.paypal.transaction_service.kafka;

import com.paypal.transaction_service.entity.Transaction;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.CompletableFuture;

@Component
public class KafkaEventProducer {

    private static final String TOPIC = "txn-initiated";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaEventProducer(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper) {

        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendTransactionEvent(String key, Transaction transaction) {

        try {

            // Convert Transaction object → JSON String
            String eventPayload = objectMapper.writeValueAsString(transaction);

            System.out.println(
                    "📤 Sending to Kafka → Topic: " + TOPIC +
                            ", Key: " + key +
                            ", Message: " + eventPayload
            );

            CompletableFuture<SendResult<String, String>> future =
                    kafkaTemplate.send(TOPIC, key, eventPayload);

            future.thenAccept(result -> {

                RecordMetadata metadata = result.getRecordMetadata();

                System.out.println(
                        "✅ Kafka message sent successfully! " +
                                "Topic: " + metadata.topic() +
                                ", Partition: " + metadata.partition() +
                                ", Offset: " + metadata.offset()
                );

            }).exceptionally(ex -> {

                System.err.println(
                        "❌ Failed to send Kafka message: " +
                                ex.getMessage()
                );

                ex.printStackTrace();

                return null;
            });

        } catch (Exception e) {

            System.err.println(
                    "❌ Failed to convert Transaction to JSON: " +
                            e.getMessage()
            );

            e.printStackTrace();
        }
    }
}