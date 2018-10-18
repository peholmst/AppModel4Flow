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

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public class Contact implements Serializable, Cloneable {

    private UUID uuid;
    private String firstName;
    private String lastName;
    private String email;

    Contact() {
    }

    Contact(String firstName, String lastName, String email) {
        this.uuid = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    @Override
    public Contact clone() {
        try {
            return (Contact) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
