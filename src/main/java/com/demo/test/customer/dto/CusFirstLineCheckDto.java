package com.demo.test.customer.dto;

public class CusFirstLineCheckDto {
    private Long id;
    private String firstLineCheckComments;

    @Override
    public String toString() {
        return "CusFirstLineCheckDto{" +
                "id=" + id +
                ", firstLineCheckComments='" + firstLineCheckComments + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstLineCheckComments() {
        return firstLineCheckComments;
    }

    public void setFirstLineCheckComments(String firstLineCheckComments) {
        this.firstLineCheckComments = firstLineCheckComments;
    }
}
