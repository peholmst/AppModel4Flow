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

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import net.pkhapps.appmodel4flow.AppModel;
import net.pkhapps.appmodel4flow.binding.group.support.FieldBindingGroupAction;

import javax.annotation.Nonnull;

class ContactDialog extends Dialog {

    ContactDialog(@Nonnull ContactController contactController, @Nonnull Contact contact) {
        final var contactModel = new ContactModel();
        final var binder = AppModel.newFieldBindingGroup();

        var uuid = new TextField("UUID");
        binder.withBinding(AppModel.bindOneWay(contactModel.uuid(), uuid, new StringTOUUIDConverter()));

        var firstName = new TextField("First name");
        binder.withBinding(AppModel.bind(contactModel.firstName(), firstName).asRequired("Please enter a first name"));

        var lastName = new TextField("Last name");
        binder.withBinding(AppModel.bind(contactModel.lastName(), lastName).asRequired("Please enter a last name"));

        var fullName = new TextField("Full name");
        binder.withBinding(AppModel.bindOneWay(contactModel.fullName(), fullName));

        var email = new TextField("E-mail");
        binder.withBinding(AppModel.bind(contactModel.email(), email).asRequired("Please enter an e-mail address"));

        Button save = new Button("Save");
        save.getElement().getThemeList().set("primary", true);

        Button cancel = new Button("Cancel");

        VerticalLayout formLayout = new VerticalLayout(uuid, firstName, lastName, fullName, email, new HorizontalLayout(save, cancel));
        formLayout.setPadding(false);

        add(formLayout);

        contactModel.read(contact);

        var commitAction = new FieldBindingGroupAction(binder, () -> contactModel.write(contact));
        var closeAction = AppModel.asAction(this::close);
        var saveAction = AppModel.compose(commitAction, contactController.saveContactAction(contact), closeAction);
        AppModel.bind(saveAction, save);
        AppModel.bind(closeAction, cancel);
    }
}
