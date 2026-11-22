package zixiu.lexicon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 一个字的完整定义：字形、展开后的部件、角色。
 */
public final class WordSpec {

    private final String glyph;
    private final List<Component> parts;
    private final WordRole role;

    public WordSpec(String glyph, List<Component> parts, WordRole role) {
        this.glyph = glyph;
        this.parts = Collections.unmodifiableList(new ArrayList<Component>(parts));
        this.role = role;
    }

    public String glyph() {
        return glyph;
    }

    public List<Component> parts() {
        return parts;
    }

    public WordRole role() {
        return role;
    }

    public boolean isUsefulResult() {
        return role == WordRole.PLAYABLE || role == WordRole.SEED;
    }

    @Override
    public String toString() {
        return glyph + "(" + role + ")";
    }
}
