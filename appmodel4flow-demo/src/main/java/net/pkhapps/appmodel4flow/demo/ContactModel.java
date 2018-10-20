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
import net.pkhapps.appmodel4flow.property.support.Combiners;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.UUID;

class ContactModel implements Serializable {

    // The properties are declared as final using their concrete types. However, they are all exposed to the
    // outside world through their interfaces.

    private final DefaultObservableValue<UUID> uuid = new DefaultObservableValue<>();
    private final DefaultProperty<String> firstName = new DefaultProperty<String>().withEmptyCheck(String::isEmpty);
    private final DefaultProperty<String> lastName = new DefaultProperty<String>().withEmptyCheck(String::isEmpty);
    private final DefaultProperty<String> email = new DefaultProperty<String>().withEmptyCheck(String::isEmpty);
    private final DefaultProperty<Integer> age = new DefaultProperty<>();
    private final CombinedValue<String> fullName = new CombinedValue<>(Combiners.joinStrings(" "),
            firstName, lastName);

    // I don't want to use the 'get' prefix for the accessor methods when they return properties and observable values.
    // I think the code flows better without the prefixes. This is however only my own personal opinion. You can name
    // your methods in any way you like.

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
    Property<Integer> age() {
        return age;
    }

    @Nonnull
    ObservableValue<String> fullName() {
        return fullName;
    }

    @Nonnull
    ObservableValue<UUID> uuid() {
        return uuid;
    }

    // These methods are used to migrate data between the model and the backend data structure. In this case,
    // Contact is just a DTO but it could also be an entity or some other object. The main point here is that the
    // model is a part of the UI. You should not use any properties or observable values in the backend.

    void read(@Nonnull Contact contact) {
        uuid.setValue(contact.getUuid());
        firstName.setCleanValue(contact.getFirstName());
        lastName.setCleanValue(contact.getLastName());
        email.setCleanValue(contact.getEmail());
        age.setCleanValue(contact.getAge());
    }

    void write(@Nonnull Contact contact) {
        contact.setUuid(uuid.getValue());
        contact.setFirstName(firstName.getValue());
        contact.setLastName(lastName.getValue());
        contact.setEmail(email.getValue());
        contact.setAge(age.getValue());
    }
}
