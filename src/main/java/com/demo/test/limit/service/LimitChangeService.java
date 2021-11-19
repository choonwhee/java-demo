package com.demo.test.limit.service;

import com.demo.test.common.jpa.CustomRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LimitChangeService {
    Logger logger = LoggerFactory.getLogger(LimitChangeService.class);

    public void changeLimit() {

        logger.info("CHANGE LIMIT");
    }
}
