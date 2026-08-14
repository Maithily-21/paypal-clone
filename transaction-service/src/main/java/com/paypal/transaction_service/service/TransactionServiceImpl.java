package com.paypal.transaction_service.service;

import com.paypal.transaction_service.entity.Transaction;
import com.paypal.transaction_service.kafka.KafkaEventProducer;
import com.paypal.transaction_service.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository repository;
    private final KafkaEventProducer kafkaEventProducer;

    @Autowired
    private RestTemplate restTemplate;

    public TransactionServiceImpl(
            TransactionRepository repository,
            KafkaEventProducer kafkaEventProducer) {

        this.repository = repository;
        this.kafkaEventProducer = kafkaEventProducer;
    }

    @Override
    public Transaction createTransaction(Transaction request) {

        System.out.println("🚀 Entered createTransaction()");

        Long senderId = request.getSenderId();
        Long receiverId = request.getReceiverId();
        Double amount = request.getAmount();

        Transaction transaction = new Transaction();

        transaction.setSenderId(senderId);
        transaction.setReceiverId(receiverId);
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus("SUCCESS");

        System.out.println(
                "📥 Incoming Transaction object: " + transaction
        );

        // Save transaction to database
        Transaction saved = repository.save(transaction);

        System.out.println(
                "💾 Saved Transaction from DB: " + saved
        );

        try {

            String key = String.valueOf(saved.getId());

            // Send Transaction object directly to Kafka
            kafkaEventProducer.sendTransactionEvent(key, saved);

            System.out.println("🚀 Kafka message sent");

        } catch (Exception e) {

            System.err.println(
                    "❌ Failed to send Kafka event: " + e.getMessage()
            );

            e.printStackTrace();
        }

        return saved;
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return repository.findAll();
    }
}