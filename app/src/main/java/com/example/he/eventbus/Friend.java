package com.example.he.eventbus;

/**
 * Created by he on 2017/5/14.
 */

public class Friend {
    private String name;
    private String password;

    public Friend(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Friend{" + "name=“" + name + "”" + "password=“" + password + "”}";
    }
}
