package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
class ApplicationShutdownManager {

    @Autowired
    private ApplicationContext appContext;

    /*
     * Invoke with `0` to indicate no error or different code to indicate
     * abnormal exit. es: shutdownManager.initiateShutdown(0);
     **/
    @Async
    public void initiateShutdown(int returnCode) throws InterruptedException {
        Thread.sleep(10000);
        SpringApplication.exit(appContext, () -> returnCode);
    }
}