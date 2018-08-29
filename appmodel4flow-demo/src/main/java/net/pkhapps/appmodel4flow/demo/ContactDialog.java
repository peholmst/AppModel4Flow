package net.pkhapps.appmodel4flow.demo;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import net.pkhapps.appmodel4flow.AppModel;
import net.pkhapps.appmodel4flow.action.ActionWithoutResult;
import net.pkhapps.appmodel4flow.action.support.FieldBindingGroupAction;

import javax.annotation.Nonnull;

class ContactDialog extends Dialog {

    ContactDialog(@Nonnull ContactController contactController, @Nonnull Contact contact) {
        final var contactModel = new ContactModel();
        final var binder = AppModel.newFieldBindingGroup();

        var uuid = new TextField("UUID");
        binder.withBinding(AppModel.bindOneWay(contactModel.getUuid(), uuid, new StringTOUUIDConverter()));

        var firstName = new TextField("First name");
        binder.withBinding(AppModel.bind(contactModel.getFirstName(), firstName).asRequired("Please enter a first name"));

        var lastName = new TextField("Last name");
        binder.withBinding(AppModel.bind(contactModel.getLastName(), lastName).asRequired("Please enter a last name"));

        var fullName = new TextField("Full name");
        binder.withBinding(AppModel.bindOneWay(contactModel.getFullName(), fullName));

        var email = new TextField("E-mail");
        binder.withBinding(AppModel.bind(contactModel.getEmail(), email).asRequired("Please enter an e-mail address"));

        Button save = new Button("Save");
        save.getElement().getThemeList().set("primary", true);

        Button cancel = new Button("Cancel");

        VerticalLayout formLayout = new VerticalLayout(uuid, firstName, lastName, fullName, email, new HorizontalLayout(save, cancel));
        formLayout.setPadding(false);

        add(formLayout);

        contactModel.read(contact);

        // TODO Create default result handler implementations that are useable in most situations

        var commitAction = new FieldBindingGroupAction(binder, () -> contactModel.write(contact));
        var closeAction = new ActionWithoutResult(this::close);
        var saveAction = AppModel.compose(commitAction, contactController.saveContactAction(contact), closeAction);
        AppModel.bind(saveAction, save);
        AppModel.bind(closeAction, cancel);
    }
}
