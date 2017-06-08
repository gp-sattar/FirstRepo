package com.grampower.attendance;

/**
 * Created by samdroid on 29/5/17.
 */

public class EmployeeWrapper {

    String email,name;

    public EmployeeWrapper(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
