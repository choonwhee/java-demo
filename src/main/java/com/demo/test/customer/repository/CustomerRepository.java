package com.demo.test.customer.repository;

import com.demo.test.common.jpa.CustomRepository;
import com.demo.test.customer.dto.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerRepository extends CustomRepository<Customer, Long> {

    @Query("select c from Customer c where 1=1 and (:id is null or c.id = :id) and" +
            "(:firstName is null or c.firstName like %:firstName%) and" +
            "(:lastName is null or c.lastName like %:lastName%) and" +
            "(:address is null or c.address like %:address%) and" +
            "(:country is null or c.address like %:country%) and" +
            "(:phone is null or c.phone like %:phone%) and" +
            "(:email is null or c.email like %:email%)")
    Page<Customer> findCustomers(Pageable pageable, @Param("id") Long id, @Param("firstName") String firstName, @Param("lastName") String lastName, @Param("address") String address, @Param("country") String country, @Param("phone") String phone, @Param("email") String email);
}
