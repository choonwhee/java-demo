package com.demo.test.common.service;

import com.demo.test.common.jpa.CustomRepository;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


public interface BaseAutoService<T, IDT extends Serializable, R extends CustomRepository<T, IDT>> {
    R getRepo();

    T saveDto(Object dto);

    T update(T updateEntity);

    T updateDto(Object dto);

}
