package zixiu.lexicon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 展开后的部件多重集。顺序无语义：两个键相等当且仅当部件与数量都相同。
 */
public final class RecipeKey {

    private final List<Component> sortedParts;

    private RecipeKey(List<Component> sortedParts) {
        this.sortedParts = Collections.unmodifiableList(sortedParts);
    }

    public static RecipeKey of(Collection<Component> parts) {
        List<Component> copy = new ArrayList<Component>(parts);
        Collections.sort(copy);
        return new RecipeKey(copy);
    }

    public List<Component> parts() {
        return sortedParts;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof RecipeKey)) {
            return false;
        }
        return sortedParts.equals(((RecipeKey) other).sortedParts);
    }

    @Override
    public int hashCode() {
        return sortedParts.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Component component : sortedParts) {
            if (sb.length() > 0) {
                sb.append('+');
            }
            sb.append(component.glyph());
        }
        return sb.toString();
    }
}
