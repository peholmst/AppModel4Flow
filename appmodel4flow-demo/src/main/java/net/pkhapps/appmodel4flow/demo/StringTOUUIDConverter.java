package net.pkhapps.appmodel4flow.demo;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

import java.util.UUID;

public class StringTOUUIDConverter implements Converter<String, UUID> {

    @Override
    public Result<UUID> convertToModel(String value, ValueContext context) {
        if (value == null) {
            return Result.ok(null);
        } else {
            try {
                return Result.ok(UUID.fromString(value));
            } catch (IllegalArgumentException ex) {
                return Result.error(ex.getMessage());
            }
        }
    }

    @Override
    public String convertToPresentation(UUID value, ValueContext context) {
        return value == null ? null : value.toString();
    }
}
