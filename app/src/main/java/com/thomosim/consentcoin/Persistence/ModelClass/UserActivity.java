package com.thomosim.consentcoin.Persistence.ModelClass;

import java.io.Serializable;
import java.util.Date;

public class UserActivity implements Serializable {
    private String activityCode;
    private String memberName;
    private String organizationName;
    private Date date;

    public UserActivity() {
    }

    public UserActivity(String activityCode, String memberName, String organizationName, Date date) {
        this.activityCode = activityCode;
        this.memberName = memberName;
        this.organizationName = organizationName;
        this.date = date;
    }

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
