package com.example.chat.entity;

import java.io.Serializable;
import java.util.Date;

public class Contact implements Serializable {
    private String id;
    private String userId;
    private String contactId;
    private Date insertTime;
    private String insertMan;
    private Date updateTime;
    private String updateMan;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    public String getInsertMan() {
        return insertMan;
    }

    public void setInsertMan(String insertMan) {
        this.insertMan = insertMan;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateMan() {
        return updateMan;
    }

    public void setUpdateMan(String updateMan) {
        this.updateMan = updateMan;
    }
}
