package com.demo.test.customer.dto;

import java.util.Arrays;
import java.util.List;

public class CustomerSearch {
    private Long[] idBtw;
    private List<String> countryInc;
    private String phone;
    private String emailInc;

    @Override
    public String toString() {
        return "CustomerSearch{" +
                "idBtw=" + Arrays.toString(idBtw) +
                ", countryInc=" + countryInc +
                ", phone='" + phone + '\'' +
                ", emailInc='" + emailInc + '\'' +
                '}';
    }

    public Long[] getIdBtw() {
        return idBtw;
    }

    public void setIdBtw(Long[] idBtw) {
        this.idBtw = idBtw;
    }

    public List<String> getCountryInc() {
        return countryInc;
    }

    public void setCountryInc(List<String> countryInc) {
        this.countryInc = countryInc;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmailInc() {
        return emailInc;
    }

    public void setEmailInc(String emailInc) {
        this.emailInc = emailInc;
    }
}
