package com.demo.test.customer.dto;

import com.demo.test.common.workflow.dto.SimpleTaskSearch;

import java.util.Arrays;
import java.util.List;

public class CustomerTaskSearch extends SimpleTaskSearch {
    private Long[] idBtw;
    private String firstNameEqi;
    private String lastNameSwi;
    private String addressEwi;
    private List<String> countryInc;
    private String phone;
    private String emailIn;

    @Override
    public String toString() {
        return "CustomerTaskSearch{" +
                "idBtw=" + Arrays.toString(idBtw) +
                ", firstNameEqi='" + firstNameEqi + '\'' +
                ", lastNameSwi='" + lastNameSwi + '\'' +
                ", addressEwi='" + addressEwi + '\'' +
                ", countryInc=" + countryInc +
                ", phone='" + phone + '\'' +
                ", emailIn='" + emailIn + '\'' +
                "} " + super.toString();
    }

    public Long[] getIdBtw() {
        return idBtw;
    }

    public void setIdBtw(Long[] idBtw) {
        this.idBtw = idBtw;
    }

    public String getFirstNameEqi() {
        return firstNameEqi;
    }

    public void setFirstNameEqi(String firstNameEqi) {
        this.firstNameEqi = firstNameEqi;
    }

    public String getLastNameSwi() {
        return lastNameSwi;
    }

    public void setLastNameSwi(String lastNameSwi) {
        this.lastNameSwi = lastNameSwi;
    }

    public String getAddressEwi() {
        return addressEwi;
    }

    public void setAddressEwi(String addressEwi) {
        this.addressEwi = addressEwi;
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

    public String getEmailIn() {
        return emailIn;
    }

    public void setEmailIn(String emailIn) {
        this.emailIn = emailIn;
    }
}
