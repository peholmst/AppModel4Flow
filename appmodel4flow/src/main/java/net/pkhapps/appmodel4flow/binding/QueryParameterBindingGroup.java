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

package net.pkhapps.appmodel4flow.binding;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.shared.Registration;
import net.pkhapps.appmodel4flow.property.ObservableValue;
import net.pkhapps.appmodel4flow.property.WritableObservableValue;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.*;

@NotThreadSafe
public class QueryParameterBindingGroup implements AfterNavigationObserver {

    private final Map<String, WritableObservableValue<List<String>>> boundParameters = new HashMap<>();

    private QueryParameters currentQueryParameters;
    private String currentPath;
    private SerializableConsumer<Exception> errorHandler;
    private boolean modelUpdateInProgress = false;

    public QueryParameterBindingGroup() {
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        this.currentQueryParameters = event.getLocation().getQueryParameters();
        this.currentPath = event.getLocation().getPath();
        var parameters = currentQueryParameters.getParameters();
        modelUpdateInProgress = true;
        try {
            boundParameters.forEach((parameterName, model) -> {
                var parameterValues = parameters.get(parameterName);
                if (parameterValues == null) {
                    model.setValue(Collections.emptyList());
                } else {
                    model.setValue(parameterValues);
                }
            });
        } catch (RuntimeException ex) {
            if (errorHandler != null) {
                errorHandler.accept(ex);
            } else {
                throw ex;
            }
        } finally {
            modelUpdateInProgress = false;
        }
    }

    private void onModelChange(@Nonnull ObservableValue.ValueChangeEvent<List<String>> valueChangeEvent) {
        if (!modelUpdateInProgress) {
            updateQueryParameters();
        }
    }

    private void updateQueryParameters() {
        Map<String, List<String>> newParameterMap = new HashMap<>();
        if (currentQueryParameters != null) {
            newParameterMap.putAll(currentQueryParameters.getParameters());
        }
        boundParameters.forEach((parameterName, model) -> newParameterMap.put(parameterName, model.getValue()));
        if (this.currentPath != null) {
            UI.getCurrent().navigate(this.currentPath, new QueryParameters(newParameterMap));
        }
    }

    /**
     * @param parameterName
     * @param model
     * @return
     */
    @Nonnull
    public Registration bindParameter(@Nonnull String parameterName,
                                      @Nonnull WritableObservableValue<List<String>> model) {
        Objects.requireNonNull(parameterName, "parameterName must not be null");
        Objects.requireNonNull(model, "model must not be null");
        if (boundParameters.put(parameterName, model) == null) {
            var registrationHandle = model.addValueChangeListener(this::onModelChange);
            return () -> {
                registrationHandle.remove();
                boundParameters.remove(parameterName);
            };
        } else {
            throw new IllegalStateException("The parameter " + parameterName + " has already been bound");
        }
    }
}
