package com.demo.test.limit.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    Logger logger = LoggerFactory.getLogger(NotificationService.class);
    public void sendNotification() {
        logger.info("SEND REJECTED NOTIFICATION");
    }
}
