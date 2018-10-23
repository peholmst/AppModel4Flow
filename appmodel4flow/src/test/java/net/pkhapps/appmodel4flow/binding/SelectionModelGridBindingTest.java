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

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.DataProvider;
import net.pkhapps.appmodel4flow.selection.DefaultSelectionModel;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link SelectionModelGridBinding}.
 */
public class SelectionModelGridBindingTest {

    private Grid<String> grid;
    private DefaultSelectionModel<String> selectionModel;
    private SelectionModelGridBinding<String> binding;

    @Before
    public void setUp() {
        grid = new Grid<>();
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.setDataProvider(DataProvider.ofItems("item1", "item2", "item3"));
        selectionModel = new DefaultSelectionModel<>();
        binding = new SelectionModelGridBinding<>(selectionModel, grid);
    }

    @Test
    public void modelSelectionIsReflectedInGrid() {
        selectionModel.select(Arrays.asList("item1", "item2"));
        assertThat(grid.getSelectedItems()).containsOnly("item1", "item2");
    }

    @Test
    public void gridSelectionIsReflectedInModel() {
        grid.select("item2");
        assertThat(grid.getSelectedItems()).containsOnly("item2");
    }

    @Test
    public void gridSelectionIsClearedWhenModelIsCleared() {
        selectionModel.selectOne("item1");
        selectionModel.clear();
        assertThat(grid.getSelectedItems()).isEmpty();
    }

    @Test
    public void modelIsClearedWhenGridSelectionIsCleared() {
        grid.select("item2");
        grid.getSelectionModel().deselectAll();
        assertThat(selectionModel.getSelection()).isEmpty();
    }

    @Test
    public void breakBinding() {
        binding.remove();
        grid.select("item1");
        assertThat(selectionModel.getSelection()).isEmpty();
    }
}
