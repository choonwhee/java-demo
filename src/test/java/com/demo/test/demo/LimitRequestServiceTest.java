package com.demo.test.demo;


import com.demo.test.DemoApplication;
import com.demo.test.common.workflow.dto.EntityTask;
import com.demo.test.limit.dto.LimitRequest;
import com.demo.test.limit.dto.LimitRequestSubmitDto;
import com.demo.test.limit.service.LimitRequestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = {DemoApplication.class})
@Transactional
public class LimitRequestServiceTest {
    Logger logger = LoggerFactory.getLogger(LimitRequestServiceTest.class);

    @Autowired
    private LimitRequestService service;

    @Test
    public void testLimitRequest() {
        LimitRequestSubmitDto dto = new LimitRequestSubmitDto();

        dto.setRequesterId("1111111");
        dto.setLimitValue(100.23);
        dto = service.saveDtoAndStartProcess(dto, "1111111");
        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();
        Map<String, Object> searchMap = new HashMap<>();
        searchMap.put("id", dto.getId());
        Pageable pageable = PageRequest.of(0, 5);
        Page<EntityTask<LimitRequest>> page = service.simpleSearchTask(pageable, searchMap);
        logger.info("LimitRequestWorkflow: "+page.getContent().get(0));
        Assertions.assertEquals(page.getContent().get(0).getEntity().getId(), dto.getId());
    }
}
