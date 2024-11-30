/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.devtop.json;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 *
 * @author raji
 */
public class KeyValue {

    @NotBlank(message = "Key should not be blank")
    public String key;

    @NotBlank(message = "Value should not be blank")
    public String value;
    public String encrypted;

    @NotNull(message = "User id can't be blank")
    public Integer user_id;
    
    public KeyValue(String key, String value, Integer user_id){
        this.key   = key;
        this.value = value;
        this.user_id = user_id;
    }
    
}
