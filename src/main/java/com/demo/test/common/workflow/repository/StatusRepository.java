package com.demo.test.common.workflow.repository;

import com.demo.test.common.jpa.CustomRepository;
import com.demo.test.common.workflow.dto.Status;
import com.demo.test.common.workflow.dto.Task;

public interface StatusRepository extends CustomRepository<Status, String> {

}

