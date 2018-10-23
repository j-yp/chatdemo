package com.example.chat.entity.vo;

import java.io.Serializable;
import java.security.Principal;

public class LoginUser implements Serializable, Principal {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1584672705947331541L;
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
