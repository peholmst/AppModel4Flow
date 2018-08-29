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
