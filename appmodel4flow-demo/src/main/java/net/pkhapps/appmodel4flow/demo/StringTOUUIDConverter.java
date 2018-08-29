/*
 * Copyright (c) 2018 the original authors (see project POM file)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
