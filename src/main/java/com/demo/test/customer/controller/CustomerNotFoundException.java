package com.demo.test.customer.controller;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(Long id) {
        super("Could not find Customer" + id);
    }
}
