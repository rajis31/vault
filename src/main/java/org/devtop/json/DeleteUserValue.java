package org.devtop.json;

import jakarta.validation.constraints.NotNull;

public class DeleteUserValue {
    @NotNull(message = "User id is required")
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
