package com.demo.test.common.workflow.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Map;

@Entity
public class ProcessInstance implements Serializable {

    private @Id String id;
    private String name;
    private String description;
    @ElementCollection
    @Fetch(FetchMode.SUBSELECT)
    @MapKeyColumn(name = "name")
    @Column(name = "value")
    private Map<String, String> variables;

    @Override
    public String toString() {
        return "WorkflowProcessInstance{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", variables=" + variables +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ProcessInstance)) return false;

        ProcessInstance that = (ProcessInstance) o;

        return new EqualsBuilder().append(id, that.id).append(name, that.name).append(description, that.description).append(variables, that.variables).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(name).append(description).append(variables).toHashCode();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, String> variables) {
        this.variables = variables;
    }
}

