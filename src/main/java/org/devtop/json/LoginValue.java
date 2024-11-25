/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.devtop.json;

import jakarta.validation.constraints.NotBlank;


/**
 * @author raji
 */
public class LoginValue {

    @NotBlank(message = "Username should not be blank")
    public String username;

    @NotBlank(message = "Password should not be blank")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginValue{" + "username=" + username + ", password=" + password + '}';
    }


}
