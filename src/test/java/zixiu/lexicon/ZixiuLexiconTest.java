package zixiu.lexicon;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ZixiuLexiconTest {

    private static List<String> glyphs(List<WordSpec> specs) {
        List<String> out = new ArrayList<String>();
        for (WordSpec spec : specs) {
            out.add(spec.glyph());
        }
        return out;
    }

    @Test
    public void tableCoversAllMultiComponentCharacters() {
        // 85 字 - 10 个单部件 = 75 个多部件字
        assertEquals(75, ZixiuLexicon.glyphCount());
    }

    @Test
    public void kouAndMuOfferFourChoices() {
        List<WordSpec> options = ZixiuLexicon.optionsFor(
                Arrays.asList(Component.KOU, Component.MU));
        assertEquals(Arrays.asList("呆", "杏", "困", "束"), glyphs(options));
    }

    @Test
    public void muAndYiOfferThreeChoices() {
        List<WordSpec> options = ZixiuLexicon.optionsFor(
                Arrays.asList(Component.MU, Component.YI));
        assertEquals(Arrays.asList("本", "末", "未"), glyphs(options));
    }

    @Test
    public void renYiYiOffersThreeChoices() {
        List<WordSpec> options = ZixiuLexicon.optionsFor(
                Arrays.asList(Component.REN, Component.YI, Component.YI));
        assertEquals(Arrays.asList("天", "夫", "仁"), glyphs(options));
    }

    @Test
    public void huiAndLvShareOneKey() {
        List<WordSpec> options = ZixiuLexicon.optionsFor(
                Arrays.asList(Component.KOU, Component.KOU));
        assertEquals(Arrays.asList("回", "吕"), glyphs(options));
    }

    @Test
    public void threeWatersProduceNothing() {
        assertTrue(ZixiuLexicon.optionsFor(
                Arrays.asList(Component.SHUI, Component.SHUI, Component.SHUI)).isEmpty());
    }

    @Test
    public void waterAndSunAreOnlyJunk() {
        assertTrue(ZixiuLexicon.optionsFor(
                Arrays.asList(Component.SHUI, Component.RI)).isEmpty());
        assertEquals(WordRole.JUNK, ZixiuLexicon.word("汨").role());
        assertEquals(WordRole.JUNK, ZixiuLexicon.word("沓").role());
    }

    @Test
    public void earthAndEarthIsASeed() {
        List<WordSpec> options = ZixiuLexicon.optionsFor(
                Arrays.asList(Component.TU, Component.TU));
        assertEquals(1, options.size());
        assertEquals("圭", options.get(0).glyph());
        assertEquals(WordRole.SEED, options.get(0).role());
    }

    @Test
    public void junkIsNeverOffered() {
        assertTrue(ZixiuLexicon.optionsFor(
                Arrays.asList(Component.SHUI, Component.SHAN)).isEmpty());
        assertFalse(ZixiuLexicon.canFuse(Arrays.asList(Component.SHUI, Component.SHAN)));
    }

    @Test
    public void junkStillSplits() {
        assertEquals(Arrays.asList(Component.SHUI, Component.SHAN),
                ZixiuLexicon.partsOf("汕"));
    }

    @Test
    public void pingSplitsIntoThreeMouths() {
        assertEquals(Arrays.asList(Component.KOU, Component.KOU, Component.KOU),
                ZixiuLexicon.partsOf("品"));
    }

    @Test
    public void yongIsIndivisible() {
        assertTrue(ZixiuLexicon.partsOf("永").isEmpty());
        assertFalse(ZixiuLexicon.canSplit("永"));
    }

    @Test
    public void liHasNoRecipeAndCannotSplit() {
        assertEquals(WordRole.NO_RECIPE, ZixiuLexicon.word("里").role());
        assertFalse(ZixiuLexicon.canSplit("里"));
    }

    @Test
    public void miaoSplitsButIsNeverProduced() {
        assertEquals(Arrays.asList(Component.SHUI, Component.SHUI, Component.SHUI),
                ZixiuLexicon.partsOf("淼"));
        assertEquals(WordRole.NO_RECIPE, ZixiuLexicon.word("淼").role());
    }

    @Test
    public void recipeKeyIgnoresSelectionOrder() {
        assertEquals(RecipeKey.of(Arrays.asList(Component.MU, Component.KOU)),
                RecipeKey.of(Arrays.asList(Component.KOU, Component.MU)));
    }

    @Test
    public void variantsNormaliseToCanonicalComponents() {
        assertEquals(Component.REN, Component.fromVariant('亻'));
        assertEquals(Component.SHUI, Component.fromVariant('氵'));
        assertEquals(Component.HUO, Component.fromVariant('灬'));
        assertEquals(Component.TU, Component.fromVariant('士'));
        assertEquals(Component.KOU, Component.fromVariant('囗'));
        assertEquals(Component.MU, Component.fromVariant('木'));
        assertNull(Component.fromVariant('田'));
    }

    @Test
    public void composedWordsReachThreePartCharacters() {
        // 回(口+口) + 口 = 品
        List<Component> huiPlusKou = new ArrayList<Component>(ZixiuLexicon.partsOf("回"));
        huiPlusKou.add(Component.KOU);
        assertEquals(Arrays.asList("品"), glyphs(ZixiuLexicon.optionsFor(huiPlusKou)));
    }

    @Test
    public void sevenOfTheEightyFiveAreNotCraftable() {
        // 淼 / 里 / 永 无配方；汕 汨 沓 哇 是废字，都拼不出来
        String[] blocked = {"淼", "里", "永", "汕", "汨", "沓", "哇"};
        for (String glyph : blocked) {
            WordSpec spec = ZixiuLexicon.word(glyph);
            assertNotNull(glyph, spec);
            assertFalse(glyph, spec.role() == WordRole.PLAYABLE);
        }
    }
}
