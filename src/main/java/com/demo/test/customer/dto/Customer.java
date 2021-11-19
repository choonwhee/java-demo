package com.demo.test.customer.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Customer {
    private @Id
    @GeneratedValue Long id;
    private String firstName;
    private String lastName;
    private String address;
    private String country;
    private String phone;
    private String email;
    private String firstLineCheckComments;
    private String finalReviewComments;

    public Customer() {
        super();
    }

    public Customer(String firstName, String lastName, String address, String country, String phone, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.country = country;
        this.phone = phone;
        this.email = email;
    }

    public Customer(String firstName, String lastName, String address, String country, String phone, String email, String firstLineCheckComments, String finalReviewComments) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.country = country;
        this.phone = phone;
        this.email = email;
        this.firstLineCheckComments = firstLineCheckComments;
        this.finalReviewComments = finalReviewComments;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", address='" + address + '\'' +
                ", country='" + country + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", firstLineCheckComments='" + firstLineCheckComments + '\'' +
                ", finalReviewComments='" + finalReviewComments + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Customer)) return false;

        Customer customer = (Customer) o;

        return new EqualsBuilder().append(id, customer.id).append(firstName, customer.firstName).append(lastName, customer.lastName).append(address, customer.address).append(country, customer.country).append(phone, customer.phone).append(email, customer.email).append(firstLineCheckComments, customer.firstLineCheckComments).append(finalReviewComments, customer.finalReviewComments).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(firstName).append(lastName).append(address).append(country).append(phone).append(email).append(firstLineCheckComments).append(finalReviewComments).toHashCode();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstLineCheckComments() {
        return firstLineCheckComments;
    }

    public void setFirstLineCheckComments(String firstLineCheckComments) {
        this.firstLineCheckComments = firstLineCheckComments;
    }

    public String getFinalReviewComments() {
        return finalReviewComments;
    }

    public void setFinalReviewComments(String finalReviewComments) {
        this.finalReviewComments = finalReviewComments;
    }
}
