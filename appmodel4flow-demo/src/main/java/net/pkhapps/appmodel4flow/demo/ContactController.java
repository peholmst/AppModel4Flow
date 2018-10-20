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

import com.vaadin.flow.data.provider.ListDataProvider;
import net.pkhapps.appmodel4flow.AppModel;
import net.pkhapps.appmodel4flow.action.Action;
import net.pkhapps.appmodel4flow.selection.Selection;
import net.pkhapps.appmodel4flow.selection.SelectionModel;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class ContactController implements Serializable {

    private final SelectionModel<Contact> contactSelectionModel = AppModel.newSelectionModel();
    private final List<Contact> contacts = new ArrayList<>();
    private final ListDataProvider<Contact> contactDataProvider = new ListDataProvider<>(contacts) {
        @Override
        public Object getId(Contact item) {
            return item.getUuid();
        }
    };
    private final Action<Void> createContactAction = AppModel.asAction(this::createContact);
    private final Action<Void> editSelectedContactAction = AppModel.asAction(
            contactSelectionModel.map(Selection::hasValue), this::editSelectedContact);

    ContactController() {
        contacts.add(new Contact("Joe", "Cool", "joecool@foo.bar", 25));
        contacts.add(new Contact("Maxwell", "Smart", "agent86@control.gov", 59));
        contacts.add(new Contact("Alice", "Anderson", "alice@crypto.foo", 43));
        contacts.add(new Contact("Bob", "Brackenreid", "bob@crypto.foo", 32));
        contacts.add(new Contact("Eve", "Enemy", "eve@drevil.com", 19));
        contactDataProvider.refreshAll();
    }

    @Nonnull
    Action<Void> createContactAction() {
        return createContactAction;
    }

    @Nonnull
    Action<Void> editContactAction(Contact contact) {
        return AppModel.asAction(() -> openDialog(contact));
    }

    @Nonnull
    Action<Void> editSelectedContactAction() {
        return editSelectedContactAction;
    }

    @Nonnull
    Action<Contact> saveContactAction(Contact contact) {
        return AppModel.asAction(() -> saveContact(contact));
    }

    @Nonnull
    SelectionModel<Contact> contactSelectionModel() {
        return contactSelectionModel;
    }

    @Nonnull
    ListDataProvider<Contact> contactDataProvider() {
        return contactDataProvider;
    }

    private void createContact() {
        openDialog(new Contact());
    }

    private void editSelectedContact() {
        contactSelectionModel.getSelection().getFirst().ifPresent(this::openDialog);
    }

    private void openDialog(Contact contact) {
        var dialog = new ContactDialog(this, contact);
        dialog.open();
    }

    private Contact saveContact(Contact contact) {
        if (contact.getUuid() == null) {
            contact.setUuid(UUID.randomUUID());
        }
        contacts.removeIf(existing -> existing.getUuid().equals(contact.getUuid()));
        contacts.add(contact.clone());
        contactDataProvider.refreshAll();
        return contact;
    }
}
