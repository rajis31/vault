/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.devtop.json;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.devtop.enums.ServiceEnum;

/**
 *
 * @author raji
 */
public class RegisterValue {

    @NotBlank(message = "Username should not be blank")
    public String username;

    @NotNull(message = "Service Level should not be blank")
    public ServiceEnum service;

    @NotBlank(message = "Password should not be blank")
    public String password;
    public String nickname;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ServiceEnum getService() {
        return service;
    }

    public void setService(ServiceEnum service) {
        this.service = service;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public RegisterValue() {
    }

    public RegisterValue(String username, ServiceEnum service, String password, String nickname) {
        this.username = username;
        this.service = service;
        this.password = password;
        this.nickname = nickname;
    }
    
    
}
