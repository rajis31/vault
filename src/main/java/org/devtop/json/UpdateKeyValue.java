package org.devtop.json;

import jakarta.validation.constraints.NotNull;

public class UpdateKeyValue {
    @NotNull(message = "Kv record id can't be blank")
    public Integer id;
    public String key;
    public String value;
    @NotNull(message = "User id can't be blank")
    public Integer user_id;
}
