package com.example.chat.entity.vo;

import java.io.Serializable;
import java.security.Principal;

public class LoginUser implements Serializable, Principal {
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public LoginUser(String name) {
        this.name = name;
    }
}
