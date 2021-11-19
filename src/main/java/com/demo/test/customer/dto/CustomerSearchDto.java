package com.demo.test.customer.dto;

import java.util.List;

public class CustomerSearchDto {
    private String idCoi;
    private String firstNameCoi;
    private String lastNameCoi;
    private List<String> countryInc;
    private String addressCoi;
    private String phoneCoi;
    private String emailCoi;

    @Override
    public String toString() {
        return "CustomerSearchDto{" +
                "idCoi='" + idCoi + '\'' +
                ", firstNameCoi='" + firstNameCoi + '\'' +
                ", lastNameCoi='" + lastNameCoi + '\'' +
                ", countryInc=" + countryInc +
                ", addressCoi='" + addressCoi + '\'' +
                ", phoneCoi='" + phoneCoi + '\'' +
                ", emailCoi='" + emailCoi + '\'' +
                '}';
    }

    public String getIdCoi() {
        return idCoi;
    }

    public void setIdCoi(String idCoi) {
        this.idCoi = idCoi;
    }

    public String getFirstNameCoi() {
        return firstNameCoi;
    }

    public void setFirstNameCoi(String firstNameCoi) {
        this.firstNameCoi = firstNameCoi;
    }

    public String getLastNameCoi() {
        return lastNameCoi;
    }

    public void setLastNameCoi(String lastNameCoi) {
        this.lastNameCoi = lastNameCoi;
    }

    public List<String> getCountryInc() {
        return countryInc;
    }

    public void setCountryInc(List<String> countryInc) {
        this.countryInc = countryInc;
    }

    public String getAddressCoi() {
        return addressCoi;
    }

    public void setAddressCoi(String addressCoi) {
        this.addressCoi = addressCoi;
    }

    public String getPhoneCoi() {
        return phoneCoi;
    }

    public void setPhoneCoi(String phoneCoi) {
        this.phoneCoi = phoneCoi;
    }

    public String getEmailCoi() {
        return emailCoi;
    }

    public void setEmailCoi(String emailCoi) {
        this.emailCoi = emailCoi;
    }
}
