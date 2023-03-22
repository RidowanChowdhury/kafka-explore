package com.example.demo.controllers;

import com.example.demo.model.GroceryItem;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class Producer {

    public static final String topic = "users";
    public static final String topic2 = "users-2";

    public static final String topic3 = "test-stream";

    public static final String topic4 = "test-stream-2";

    private final KafkaTemplate kafkaTemplate;

    public static int key = 0;


    @KafkaListener(topics = topic)
    public void getMessage(GroceryItem message) {
        System.out.println(message);
    }
    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        kafkaTemplate.send(topic, GroceryItem.builder().id("1").name("egg").quantity(25).category("protein").build());
    }
    @KafkaListener(topics = topic2)
    public void getMessage2(String message) {
        System.out.println(message);
    }
    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup2() {
        kafkaTemplate.send(topic2, "Neil");
    }

    @KafkaListener(topics = topic4)
    public void getStreamMessage(String message) {
        System.out.println("From topic-4: "+message);
    }

    @KafkaListener(topics = topic3)
    public void getStreamMessagebb(String message) {
        System.out.println("From topic-3: "+message);
    }
    public void updateStreamMessage(String message) {
        key+=1;
        kafkaTemplate.send(topic3, key+"", message);

    }
}
