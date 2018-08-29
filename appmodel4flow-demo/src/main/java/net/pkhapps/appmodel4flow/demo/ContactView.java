package net.pkhapps.appmodel4flow.demo;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import net.pkhapps.appmodel4flow.AppModel;

@Route("")
public class ContactView extends VerticalLayout {

    private final ContactController contactController = new ContactController();

    public ContactView() {
        Grid<Contact> contactGrid = new Grid<>();
        contactGrid.addColumn(Contact::getFirstName).setHeader("First name");
        contactGrid.addColumn(Contact::getLastName).setHeader("Last name");
        contactGrid.addColumn(Contact::getEmail).setHeader("E-mail");
        contactGrid.addColumn(new ComponentRenderer<>(contact -> {
            var editButton = new Button("Edit...");
            AppModel.bind(contactController.editContactAction(contact), editButton);
            return editButton;
        })).setHeader("Actions");
        contactGrid.setDataProvider(contactController.contactDataProvider());
        AppModel.bind(contactController.contactSelectionModel(), contactGrid);

        ComboBox<Contact> contactComboBox = new ComboBox<>();
        contactComboBox.setDataProvider(contactController.contactDataProvider());
        contactComboBox.setItemLabelGenerator(contact -> String.format("%s %s", contact.getFirstName(), contact.getLastName()));
        AppModel.bind(contactController.contactSelectionModel(), contactComboBox);

        Button createContact = new Button("Create Contact...");
        AppModel.bind(contactController.createContactAction(), createContact);

        Button editSelectedContact = new Button("Edit Selected...");
        AppModel.bind(contactController.editSelectedContactAction(), editSelectedContact);

        add(contactGrid, contactComboBox, createContact, editSelectedContact);
    }
}
