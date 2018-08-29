package net.pkhapps.appmodel4flow.selection;

import net.pkhapps.appmodel4flow.property.DefaultObservableValue;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Collection;

/**
 * Default implementation of {@link SelectionModel}. Developers are free to use this whenever they need an
 * implementation of the model interface.
 *
 * @param <T> the type of the items in the selection.
 */
@NotThreadSafe
public class DefaultSelectionModel<T> extends DefaultObservableValue<Selection<T>> implements SelectionModel<T> {

    public DefaultSelectionModel() {
        super(new DefaultSelection<>());
    }

    @Override
    public void select(@Nonnull Collection<T> items) {
        setValue(new DefaultSelection<>(items));
    }
}
