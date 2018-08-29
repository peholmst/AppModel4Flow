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

package net.pkhapps.appmodel4flow.property;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link DefaultProperty}.
 */
public class DefaultPropertyTest {

    @Test
    public void dirtyFlag_falseByDefault() {
        DefaultProperty<String> property = new DefaultProperty<>();
        assertThat(property.isDirty().getValue()).isFalse();
    }

    @Test
    public void dirtyFlag_trueAfterSettingValue() {
        DefaultProperty<String> property = new DefaultProperty<>();
        property.setValue("hello");
        assertThat(property.isDirty().getValue()).isTrue();
    }

    @Test
    public void dirtyFlag_falseAfterResetting() {
        DefaultProperty<String> property = new DefaultProperty<>();
        property.setValue("hello");
        property.resetDirtyFlag();
        assertThat(property.isDirty().getValue()).isFalse();
    }

    @Test
    public void dirtyFlag_falseAfterSettingCleanValue() {
        DefaultProperty<String> property = new DefaultProperty<>();
        property.setCleanValue("hello");
        assertThat(property.isDirty().getValue()).isFalse();
    }

    @Test
    public void dirtyFlag_falseAfterSettingValueBackToOriginal() {
        DefaultProperty<String> property = new DefaultProperty<>("hello");
        property.setValue("world");
        assertThat(property.isDirty().getValue()).isTrue();
        property.setValue("hello");
        assertThat(property.isDirty().getValue()).isFalse();
    }

    @Test(expected = Property.ReadOnlyException.class)
    public void readOnlyFlag_settingValueThrowsException() {
        DefaultProperty<String> property = new DefaultProperty<>();
        property.setReadOnly(true);
        assertThat(property.isReadOnly().getValue()).isTrue();
        property.setValue("hello");
    }

    @Test
    public void discard_noFlagReset_valueSetToNull() {
        DefaultProperty<String> property = new DefaultProperty<>();
        property.setValue("hello");
        property.discard();
        assertThat(property.getValue()).isNull();
        assertThat(property.isDirty().getValue()).isFalse();
    }

    @Test
    public void discard_flagReset_valueSetToOld() {
        DefaultProperty<String> property = new DefaultProperty<>();
        property.setValue("hello");
        property.resetDirtyFlag();
        property.setValue("world");
        property.discard();
        assertThat(property.getValue()).isEqualTo("hello");
        assertThat(property.isDirty().getValue()).isFalse();
    }
}
