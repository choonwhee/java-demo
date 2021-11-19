package com.demo.test.limit.repository;

import com.demo.test.common.jpa.CustomRepository;
import com.demo.test.customer.dto.Customer;
import com.demo.test.limit.dto.LimitRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LimitRequestRepository extends CustomRepository<LimitRequest, Long> {
}
