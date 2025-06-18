package com.SCM.role;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

public class RoleTypeDeserializer extends JsonDeserializer<Role.RoleType> {

    @Override
    public Role.RoleType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String value = p.getValueAsString();
        try {
            return Role.RoleType.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new IOException("Invalid value for RoleType: " + value, e);
        }
    }
}
