package com.demo.test.limit.service;

import com.demo.test.common.service.AutoService;
import com.demo.test.common.service.BaseAutoWorkflowService;

import com.demo.test.common.workflow.dto.EntityTask;
import com.demo.test.limit.dto.LimitRequest;
import com.demo.test.limit.dto.LimitRequestSubmitDto;
import com.demo.test.limit.repository.LimitRequestRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AutoService(entityClass = LimitRequest.class, entityRepoClass = LimitRequestRepository.class)
public interface LimitRequestService extends BaseAutoWorkflowService<LimitRequest, Long, LimitRequestRepository> {

    public LimitRequestSubmitDto saveDtoAndStartProcess(LimitRequestSubmitDto limitRequest, String userId);

    public Page<EntityTask<LimitRequest>> simpleSearchTask(Pageable pageable, Map<String, Object> searchMap);

}
