package com.LoginService.login.producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AccountCreationProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public AccountCreationProducer(KafkaTemplate<String,Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public void sendMessage(Object message, String topic) {
        kafkaTemplate.send(topic, message);
        System.out.println("Message sent: " + message);
    }
}
