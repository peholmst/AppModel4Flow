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

package net.pkhapps.appmodel4flow.binding.group.support;

import net.pkhapps.appmodel4flow.binding.group.FieldBindingGroup;
import net.pkhapps.appmodel4flow.property.DefaultProperty;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link FieldBindingGroupAction}.
 */
public class FieldBindingGroupActionTest {

    private DefaultProperty<Boolean> dirty;
    private DefaultProperty<Boolean> presentationValid;
    private DefaultProperty<Boolean> modelValid;
    private FieldBindingGroup fieldBindingGroupMock;

    @Before
    public void setUp() {
        dirty = new DefaultProperty<>(false);
        presentationValid = new DefaultProperty<>(false);
        modelValid = new DefaultProperty<>(false);
        fieldBindingGroupMock = mock(FieldBindingGroup.class);
        when(fieldBindingGroupMock.isDirty()).thenReturn(dirty);
        when(fieldBindingGroupMock.isPresentationValid()).thenReturn(presentationValid);
        when(fieldBindingGroupMock.isModelValid()).thenReturn(modelValid);
    }

    @Test
    public void initialState() {
        var action = new FieldBindingGroupAction(fieldBindingGroupMock);
        assertThat(action.isPerformable().getValue()).isFalse();
        assertThat(action.getFieldBindingGroup()).isSameAs(fieldBindingGroupMock);
    }

    @Test
    public void isPerformable_onlyDirtyIsTrue_notPerformable() {
        var action = new FieldBindingGroupAction(fieldBindingGroupMock);
        dirty.setValue(true);
        assertThat(action.isPerformable().getValue()).isFalse();
    }

    @Test
    public void isPerformable_onlyPresentationValidIsTrue_notPerformable() {
        var action = new FieldBindingGroupAction(fieldBindingGroupMock);
        presentationValid.setValue(true);
        assertThat(action.isPerformable().getValue()).isFalse();
    }

    @Test
    public void isPerformable_onlyModelValidIsTrue_notPerformable() {
        var action = new FieldBindingGroupAction(fieldBindingGroupMock);
        modelValid.setValue(true);
        assertThat(action.isPerformable().getValue()).isFalse();
    }

    @Test
    public void isPerformable_allFlagsAreTrue_performable() {
        var action = new FieldBindingGroupAction(fieldBindingGroupMock);
        dirty.setValue(true);
        presentationValid.setValue(true);
        modelValid.setValue(true);
        assertThat(action.isPerformable().getValue()).isTrue();
    }
}
