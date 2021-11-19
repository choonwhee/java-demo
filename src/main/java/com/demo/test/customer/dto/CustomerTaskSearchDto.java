package com.demo.test.customer.dto;

public class CustomerTaskSearchDto {
    private String entityIdCoi;
    private String entityFirstNameCoi;
    private String entityLastNameCoi;
    private String entityAddressCoi;
    private String entityCountryInc;
    private String entityPhoneCoi;
    private String entityEmailCoi;
    private String taskTaskStatusInc;
    private String taskCreatedDateBtw;

    @Override
    public String toString() {
        return "CustomerTaskSearchDto{" +
                "entityIdCoi='" + entityIdCoi + '\'' +
                ", entityFirstNameCoi='" + entityFirstNameCoi + '\'' +
                ", entityLastNameCoi='" + entityLastNameCoi + '\'' +
                ", entityAddressCoi='" + entityAddressCoi + '\'' +
                ", entityCountryInc='" + entityCountryInc + '\'' +
                ", entityPhoneCoi='" + entityPhoneCoi + '\'' +
                ", entityEmailCoi='" + entityEmailCoi + '\'' +
                ", taskTaskStatusInc='" + taskTaskStatusInc + '\'' +
                ", taskCreatedDateBtw='" + taskCreatedDateBtw + '\'' +
                '}';
    }

    public String getEntityIdCoi() {
        return entityIdCoi;
    }

    public void setEntityIdCoi(String entityIdCoi) {
        this.entityIdCoi = entityIdCoi;
    }

    public String getEntityFirstNameCoi() {
        return entityFirstNameCoi;
    }

    public void setEntityFirstNameCoi(String entityFirstNameCoi) {
        this.entityFirstNameCoi = entityFirstNameCoi;
    }

    public String getEntityLastNameCoi() {
        return entityLastNameCoi;
    }

    public void setEntityLastNameCoi(String entityLastNameCoi) {
        this.entityLastNameCoi = entityLastNameCoi;
    }

    public String getEntityAddressCoi() {
        return entityAddressCoi;
    }

    public void setEntityAddressCoi(String entityAddressCoi) {
        this.entityAddressCoi = entityAddressCoi;
    }

    public String getEntityCountryInc() {
        return entityCountryInc;
    }

    public void setEntityCountryInc(String entityCountryInc) {
        this.entityCountryInc = entityCountryInc;
    }

    public String getEntityPhoneCoi() {
        return entityPhoneCoi;
    }

    public void setEntityPhoneCoi(String entityPhoneCoi) {
        this.entityPhoneCoi = entityPhoneCoi;
    }

    public String getEntityEmailCoi() {
        return entityEmailCoi;
    }

    public void setEntityEmailCoi(String entityEmailCoi) {
        this.entityEmailCoi = entityEmailCoi;
    }

    public String getTaskTaskStatusInc() {
        return taskTaskStatusInc;
    }

    public void setTaskTaskStatusInc(String taskTaskStatusInc) {
        this.taskTaskStatusInc = taskTaskStatusInc;
    }

    public String getTaskCreatedDateBtw() {
        return taskCreatedDateBtw;
    }

    public void setTaskCreatedDateBtw(String taskCreatedDateBtw) {
        this.taskCreatedDateBtw = taskCreatedDateBtw;
    }
}
