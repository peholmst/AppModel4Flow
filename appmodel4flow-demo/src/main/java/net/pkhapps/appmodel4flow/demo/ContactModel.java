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
    Property<String> getFirstName() {
        return firstName;
    }

    @Nonnull
    Property<String> getLastName() {
        return lastName;
    }

    @Nonnull
    Property<String> getEmail() {
        return email;
    }

    @Nonnull
    ObservableValue<String> getFullName() {
        return fullName;
    }

    @Nonnull
    ObservableValue<UUID> getUuid() {
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
