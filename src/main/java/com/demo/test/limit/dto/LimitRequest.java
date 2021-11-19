package com.demo.test.limit.dto;

import com.demo.test.common.user.dto.User;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;

@Entity
public class LimitRequest {
    @Id
    @GeneratedValue
    Long id;

    String requesterId;
    Double limitValue;
    String reviewerComments;

    @Override
    public String toString() {
        return "LimitRequest{" +
                "id=" + id +
                ", requesterId='" + requesterId + '\'' +
                ", limitValue=" + limitValue +
                ", reviewerComments='" + reviewerComments + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof LimitRequest)) return false;

        LimitRequest that = (LimitRequest) o;

        return new EqualsBuilder().append(id, that.id).append(requesterId, that.requesterId).append(limitValue, that.limitValue).append(reviewerComments, that.reviewerComments).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(requesterId).append(limitValue).append(reviewerComments).toHashCode();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public Double getLimitValue() {
        return limitValue;
    }

    public void setLimitValue(Double limitValue) {
        this.limitValue = limitValue;
    }

    public String getReviewerComments() {
        return reviewerComments;
    }

    public void setReviewerComments(String reviewerComments) {
        this.reviewerComments = reviewerComments;
    }
}
