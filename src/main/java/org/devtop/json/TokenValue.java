package org.devtop.json;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TokenValue {
    @NotBlank(message = "Id cannot be null")
    public String role;

    @NotNull(message = "Id cannot be null")
    public int id;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
