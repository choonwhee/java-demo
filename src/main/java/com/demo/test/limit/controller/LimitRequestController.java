package com.demo.test.limit.controller;

import com.demo.test.common.workflow.dto.EntityTask;
import com.demo.test.common.workflow.service.WorkflowService;
import com.demo.test.limit.dto.LimitRequest;
import com.demo.test.limit.dto.LimitRequestSubmitDto;
import com.demo.test.limit.service.LimitRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/rest")
public class LimitRequestController {

    Logger logger = LoggerFactory.getLogger(LimitRequestController.class);

    private final LimitRequestService service;
    private final WorkflowService wfSvc;

    public LimitRequestController(LimitRequestService service, WorkflowService wfSvc) {
        this.service = service;
        this.wfSvc = wfSvc;
    }

    @PostMapping("/limit_requests")
    LimitRequestSubmitDto saveLimitRequest(Principal principal, LimitRequestSubmitDto dto) {
        return service.saveDtoAndStartProcess(dto, principal.getName());
    }

    @GetMapping("/limit_requests")
    Page<EntityTask<LimitRequest>> search(Pageable pageable, @Param("idEql") Long idEql, String requesterIdCon, Double limitValue) {
        Map<String, Object> searchMap = new HashMap<>();
        searchMap.put("idEql", idEql);
        searchMap.put("requester.idCon", requesterIdCon);
        searchMap.put("limitValue", limitValue);

        return service.simpleSearchTask(pageable, searchMap);
    }

}
