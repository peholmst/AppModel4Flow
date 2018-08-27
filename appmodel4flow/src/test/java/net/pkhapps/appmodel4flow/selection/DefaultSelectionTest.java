package net.pkhapps.appmodel4flow.selection;

import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link DefaultSelection}.
 */
@SuppressWarnings("unchecked")
public class DefaultSelectionTest {

    @Test
    public void equalsAndHashCode() {
        var firstSelection = new DefaultSelection<>(Set.of("hello", "world"));
        var secondSelection = new DefaultSelection<>(Set.of("hello", "world"));

        assertThat(firstSelection).isEqualTo(secondSelection);
        assertThat(firstSelection.hashCode()).isEqualTo(secondSelection.hashCode());
        assertThat(firstSelection).isEqualTo(firstSelection);
        assertThat(firstSelection).isNotEqualTo(null);
    }

    @Test
    public void isEmpty_emptySelection_returnsTrue() {
        var emptySelection = new DefaultSelection<>();
        assertThat(emptySelection.isEmpty()).isTrue();
    }

    @Test
    public void isEmpty_nonEmptySelection_returnsFalse() {
        var nonEmptySelection = new DefaultSelection<>(Set.of("hello", "world"));
        assertThat(nonEmptySelection.hasValue()).isTrue();
    }

    @Test
    public void stream_containsItemsInSelection() {
        var selection = new DefaultSelection<>(List.of("hello", "world"));
        assertThat(selection.stream()).containsExactly("hello", "world");
    }

    @Test
    public void iterator_containsItemsInSelection() {
        var selection = new DefaultSelection<>(List.of("hello", "world"));
        assertThat(selection).containsExactly("hello", "world");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void iterator_removeNotSupported() {
        var selection = new DefaultSelection<>(List.of("hello", "world"));
        var iterator = selection.iterator();
        iterator.remove();
    }
}
