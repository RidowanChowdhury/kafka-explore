package com.example.demo.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class BaseController {

   private final ApplicationShutdownManager applicationShutdownManager;
   private final Producer producer;

    public BaseController(ApplicationShutdownManager applicationShutdownManager, Producer producer) {
        this.applicationShutdownManager = applicationShutdownManager;
        this.producer = producer;
    }

    @GetMapping("/")
    public String rootApi() throws InterruptedException {
//        applicationShutdownManager.initiateShutdown(0);
        return new String("Current date time is: ").concat(new Date().toString());
    }

    @GetMapping("/kafka/{data}")
    public ResponseEntity<String> getKafkaMessage(@PathVariable String data) {
        producer.updateStreamMessage(data);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    @GetMapping("/kafka/input")
    public ResponseEntity<String> getKafkaMessage2() {
        List<String> test = new ArrayList<>();
        test.add("hello world");
        test.add("hello");
        test.add(("hello world, ridwan"));
        for(String data: test) {
            producer.updateStreamMessage(data);
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }

}