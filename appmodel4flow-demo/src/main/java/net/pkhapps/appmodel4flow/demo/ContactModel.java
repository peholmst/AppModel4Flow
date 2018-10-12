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

import net.pkhapps.appmodel4flow.property.*;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.UUID;

class ContactModel implements Serializable {

    private final DefaultObservableValue<UUID> uuid = new DefaultObservableValue<>();
    private final DefaultProperty<String> firstName = new DefaultProperty<>();
    private final DefaultProperty<String> lastName = new DefaultProperty<>();
    private final DefaultProperty<String> email = new DefaultProperty<>();
    private final ComputedValue<String> fullName = new ComputedValue<>(
            () -> String.format("%s %s", firstName.getValue(), lastName.getValue()),
            firstName, lastName);

    @Nonnull
    Property<String> firstName() {
        return firstName;
    }

    @Nonnull
    Property<String> lastName() {
        return lastName;
    }

    @Nonnull
    Property<String> email() {
        return email;
    }

    @Nonnull
    ObservableValue<String> fullName() {
        return fullName;
    }

    @Nonnull
    ObservableValue<UUID> uuid() {
        return uuid;
    }

    void read(@Nonnull Contact contact) {
        uuid.setValue(contact.getUuid());
        firstName.setCleanValue(contact.getFirstName());
        lastName.setCleanValue(contact.getLastName());
        email.setCleanValue(contact.getEmail());
    }

    void write(@Nonnull Contact contact) {
        contact.setUuid(uuid.getValue());
        contact.setFirstName(firstName.getValue());
        contact.setLastName(lastName.getValue());
        contact.setEmail(email.getValue());
    }
}
