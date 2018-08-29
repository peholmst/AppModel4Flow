package net.pkhapps.appmodel4flow.demo;

import com.vaadin.flow.data.provider.ListDataProvider;
import net.pkhapps.appmodel4flow.AppModel;
import net.pkhapps.appmodel4flow.action.AbstractAction;
import net.pkhapps.appmodel4flow.action.Action;
import net.pkhapps.appmodel4flow.action.ActionWithoutResult;
import net.pkhapps.appmodel4flow.selection.SelectionModel;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class ContactController implements Serializable {

    private final SelectionModel<Contact> contactSelectionModel = AppModel.newSelectionModel();
    private final List<Contact> contacts = new ArrayList<>();
    private final ListDataProvider<Contact> contactDataProvider = new ListDataProvider<>(contacts);

    abstract class ContactDialogAction extends ActionWithoutResult {

        void openDialog(Contact contact) {
            var dialog = new ContactDialog(ContactController.this, contact);
            dialog.open();
        }
    }

    private final Action<Void> createContactAction = new ContactDialogAction() {

        @Override
        protected void doPerformWithoutResult() {
            openDialog(new Contact());
        }
    };

    private final Action<Void> editSelectedContactAction = new ContactDialogAction() {
        {
            contactSelectionModel.addValueChangeListener(event -> fireStateChangeEvent());
        }

        @Override
        public boolean isPerformable() {
            return contactSelectionModel.getSelection().hasValue();
        }

        @Override
        protected void doPerformWithoutResult() {
            contactSelectionModel.getSelection().getFirst().ifPresent(this::openDialog);
        }
    };

    ContactController() {
        contacts.add(new Contact("Joe", "Cool", "joecool@foo.bar"));
        contacts.add(new Contact("Maxwell", "Smart", "agent86@control.gov"));
        contacts.add(new Contact("Alice", "Anderson", "alice@crypto.foo"));
        contacts.add(new Contact("Bob", "Brackenreid", "bob@crypto.foo"));
        contacts.add(new Contact("Eve", "Enemy", "eve@drevil.com"));
        contactDataProvider.refreshAll();
    }

    @Nonnull
    Action<Void> createContactAction() {
        return createContactAction;
    }

    @Nonnull
    Action<Void> editContactAction(Contact contact) {
        return new ContactDialogAction() {

            @Override
            protected void doPerformWithoutResult() {
                openDialog(contact);
            }
        };
    }

    @Nonnull
    Action<Void> editSelectedContactAction() {
        return editSelectedContactAction;
    }

    @Nonnull
    Action<Contact> saveContactAction(Contact contact) {
        return new AbstractAction<>() {
            @Override
            protected Contact doPerform() {
                if (contact.getUuid() == null) {
                    contact.setUuid(UUID.randomUUID());
                }
                contacts.removeIf(existing -> existing.getUuid().equals(contact.getUuid()));
                contacts.add(contact.clone());
                contactDataProvider.refreshAll();
                return contact;
            }
        };
    }

    @Nonnull
    SelectionModel<Contact> contactSelectionModel() {
        return contactSelectionModel;
    }

    @Nonnull
    ListDataProvider<Contact> contactDataProvider() {
        return contactDataProvider;
    }
}
