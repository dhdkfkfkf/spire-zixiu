package zixiu.lexicon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 字学核心：汉字规则的唯一真相来源。
 *
 * 规则只在这里判定，游戏层（卡牌、遗物、UI）只做适配。
 * 本类不引用任何游戏引擎类型。
 *
 * 表里 75 个多部件字，加上 10 个单部件（人 口 木 月 土 一 水 火 山 日）共 85 字。
 * 拆解按展开后的部件多重集记录，顺序无语义。
 */
public final class ZixiuLexicon {

    private static final Map<RecipeKey, List<WordSpec>> RECIPES =
            new LinkedHashMap<RecipeKey, List<WordSpec>>();
    private static final Map<String, WordSpec> BY_GLYPH =
            new LinkedHashMap<String, WordSpec>();

    static {
        // ---- 两部件 ----
        add("从", WordRole.PLAYABLE, Component.REN, Component.REN);
        add("囚", WordRole.PLAYABLE, Component.KOU, Component.REN);
        add("休", WordRole.PLAYABLE, Component.REN, Component.MU);
        add("伙", WordRole.PLAYABLE, Component.REN, Component.HUO);
        add("仙", WordRole.PLAYABLE, Component.REN, Component.SHAN);
        add("仕", WordRole.PLAYABLE, Component.REN, Component.TU);
        add("大", WordRole.PLAYABLE, Component.REN, Component.YI);
        add("二", WordRole.PLAYABLE, Component.YI, Component.YI);

        add("回", WordRole.PLAYABLE, Component.KOU, Component.KOU);
        add("吕", WordRole.PLAYABLE, Component.KOU, Component.KOU);

        add("呆", WordRole.PLAYABLE, Component.KOU, Component.MU);
        add("杏", WordRole.PLAYABLE, Component.MU, Component.KOU);
        add("困", WordRole.PLAYABLE, Component.KOU, Component.MU);
        add("束", WordRole.PLAYABLE, Component.MU, Component.KOU);

        add("本", WordRole.PLAYABLE, Component.MU, Component.YI);
        add("末", WordRole.PLAYABLE, Component.YI, Component.MU);
        add("未", WordRole.PLAYABLE, Component.MU, Component.YI);

        add("吐", WordRole.PLAYABLE, Component.KOU, Component.TU);
        add("吉", WordRole.PLAYABLE, Component.TU, Component.KOU);

        add("昌", WordRole.PLAYABLE, Component.RI, Component.RI);
        add("林", WordRole.PLAYABLE, Component.MU, Component.MU);
        add("炎", WordRole.PLAYABLE, Component.HUO, Component.HUO);
        add("朋", WordRole.PLAYABLE, Component.YUE, Component.YUE);
        add("出", WordRole.PLAYABLE, Component.SHAN, Component.SHAN);

        add("沐", WordRole.PLAYABLE, Component.SHUI, Component.MU);
        add("杜", WordRole.PLAYABLE, Component.MU, Component.TU);
        add("灿", WordRole.PLAYABLE, Component.HUO, Component.SHAN);
        add("明", WordRole.PLAYABLE, Component.RI, Component.YUE);
        add("灭", WordRole.PLAYABLE, Component.YI, Component.HUO);
        add("旦", WordRole.PLAYABLE, Component.RI, Component.YI);
        add("王", WordRole.PLAYABLE, Component.YI, Component.TU);
        add("杰", WordRole.PLAYABLE, Component.MU, Component.HUO);

        add("杲", WordRole.PLAYABLE, Component.RI, Component.MU);
        add("杳", WordRole.PLAYABLE, Component.MU, Component.RI);
        add("灶", WordRole.PLAYABLE, Component.HUO, Component.TU);
        add("炅", WordRole.PLAYABLE, Component.RI, Component.HUO);
        add("肚", WordRole.PLAYABLE, Component.YUE, Component.TU);
        add("且", WordRole.PLAYABLE, Component.YUE, Component.YI);

        // ---- 三部件 ----
        add("众", WordRole.PLAYABLE, Component.REN, Component.REN, Component.REN);
        add("品", WordRole.PLAYABLE, Component.KOU, Component.KOU, Component.KOU);
        add("森", WordRole.PLAYABLE, Component.MU, Component.MU, Component.MU);
        add("焱", WordRole.PLAYABLE, Component.HUO, Component.HUO, Component.HUO);
        add("晶", WordRole.PLAYABLE, Component.RI, Component.RI, Component.RI);
        add("三", WordRole.PLAYABLE, Component.YI, Component.YI, Component.YI);
        add("垚", WordRole.PLAYABLE, Component.TU, Component.TU, Component.TU);

        add("保", WordRole.PLAYABLE, Component.REN, Component.KOU, Component.MU);
        add("合", WordRole.PLAYABLE, Component.REN, Component.YI, Component.KOU);
        add("体", WordRole.PLAYABLE, Component.REN, Component.MU, Component.YI);
        add("全", WordRole.PLAYABLE, Component.REN, Component.TU, Component.YI);
        add("坐", WordRole.PLAYABLE, Component.REN, Component.REN, Component.TU);
        add("丛", WordRole.PLAYABLE, Component.REN, Component.REN, Component.YI);
        add("因", WordRole.PLAYABLE, Component.KOU, Component.REN, Component.YI);

        add("佳", WordRole.PLAYABLE, Component.REN, Component.TU, Component.TU);
        add("桂", WordRole.PLAYABLE, Component.MU, Component.TU, Component.TU);
        add("洼", WordRole.PLAYABLE, Component.SHUI, Component.TU, Component.TU);

        add("天", WordRole.PLAYABLE, Component.REN, Component.YI, Component.YI);
        add("夫", WordRole.PLAYABLE, Component.REN, Component.YI, Component.YI);
        add("仁", WordRole.PLAYABLE, Component.REN, Component.YI, Component.YI);
        add("击", WordRole.PLAYABLE, Component.SHAN, Component.YI, Component.YI);

        add("唱", WordRole.PLAYABLE, Component.KOU, Component.RI, Component.RI);
        add("查", WordRole.PLAYABLE, Component.MU, Component.RI, Component.YI);
        add("胆", WordRole.PLAYABLE, Component.YUE, Component.RI, Component.YI);
        add("但", WordRole.PLAYABLE, Component.REN, Component.RI, Component.YI);
        add("崩", WordRole.PLAYABLE, Component.SHAN, Component.YUE, Component.YUE);
        add("淡", WordRole.PLAYABLE, Component.SHUI, Component.HUO, Component.HUO);
        add("焚", WordRole.PLAYABLE, Component.MU, Component.MU, Component.HUO);
        add("淋", WordRole.PLAYABLE, Component.SHUI, Component.MU, Component.MU);

        // ---- 字胚：拼得出、打不出，只作更大字的构件 ----
        add("圭", WordRole.SEED, Component.TU, Component.TU);

        // ---- 废字：拼得出来，但没有正面用途 ----
        add("汕", WordRole.PLAYABLE, Component.SHUI, Component.SHAN);
        add("汨", WordRole.PLAYABLE, Component.SHUI, Component.RI);
        add("沓", WordRole.PLAYABLE, Component.SHUI, Component.RI);
        add("哇", WordRole.PLAYABLE, Component.KOU, Component.TU, Component.TU);

        // ---- 无配方：拼不出来，但拆得开 / 拆不开 ----
        add("淼", WordRole.PLAYABLE, Component.SHUI, Component.SHUI, Component.SHUI);
        add("里", WordRole.NO_RECIPE);
        add("永", WordRole.NO_RECIPE);
    }

    private ZixiuLexicon() {
    }

    private static void add(String glyph, WordRole role, Component... parts) {
        List<Component> partList = new ArrayList<Component>();
        Collections.addAll(partList, parts);
        WordSpec spec = new WordSpec(glyph, partList, role);
        BY_GLYPH.put(glyph, spec);

        if (partList.isEmpty()) {
            return;
        }
        RecipeKey key = RecipeKey.of(partList);
        List<WordSpec> bucket = RECIPES.get(key);
        if (bucket == null) {
            bucket = new ArrayList<WordSpec>();
            RECIPES.put(key, bucket);
        }
        bucket.add(spec);
    }

    /**
     * 这组部件能拼出的有用结果（可打出或字胚）。废字与无配方组合不返回。
     * 顺序稳定，等于配方表的登记顺序。
     */
    public static List<WordSpec> optionsFor(Collection<Component> parts) {
        List<WordSpec> bucket = RECIPES.get(RecipeKey.of(parts));
        if (bucket == null) {
            return Collections.emptyList();
        }
        List<WordSpec> result = new ArrayList<WordSpec>();
        for (WordSpec spec : bucket) {
            if (spec.isUsefulResult()) {
                result.add(spec);
            }
        }
        return result;
    }

    /**
     * 一个字的展开部件。无配方、独体字返回空列表。
     */
    public static List<Component> partsOf(String glyph) {
        WordSpec spec = BY_GLYPH.get(glyph);
        if (spec == null) {
            return Collections.emptyList();
        }
        return spec.parts();
    }

    public static boolean canFuse(Collection<Component> parts) {
        return !optionsFor(parts).isEmpty();
    }

    /**
     * 拆解前置：有展开部件才拆得开。独体字（永）返回 false。
     */
    public static boolean canSplit(String glyph) {
        return !partsOf(glyph).isEmpty();
    }

    public static WordSpec word(String glyph) {
        return BY_GLYPH.get(glyph);
    }

    public static int glyphCount() {
        return BY_GLYPH.size();
    }
}
