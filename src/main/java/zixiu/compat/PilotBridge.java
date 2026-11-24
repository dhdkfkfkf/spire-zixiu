package zixiu.compat;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import zixiu.cards.CardComponents;
import zixiu.core.FusionTracker;
import zixiu.lexicon.Component;
import zixiu.lexicon.WordSpec;
import zixiu.lexicon.ZixiuLexicon;
import zixiu.powers.HePower;
import zixiu.ui.HandActions;

import java.util.ArrayList;
import java.util.List;

/**
 * 给外部求解器（spire-pilot）用的稳定接口。
 *
 * 只做四件事：判断合/拆是否合法、列出可选结果、在**当前世界**里同步执行一次合/拆、读本回合计数。
 * 同步执行意味着不走 VFX（{@code effectList}），牌直接被放进手牌，沙盒里也能落地。
 *
 * 求解器通过反射调用这些静态方法即可，不需要依赖本模组的任何内部类型：
 *
 * <pre>
 * boolean canFuse(AbstractCard)
 * List&lt;String&gt; fusionResults(List&lt;AbstractCard&gt;)   // 字的列表，空表示合不出
 * boolean canSplit(AbstractCard)
 * List&lt;String&gt; splitPartNames(AbstractCard)            // 部件枚举名，如 REN / KOU
 * boolean isFresh(AbstractCard)                        // 墨迹未干
 * int splitsThisTurn() / int fusionsThisTurn()
 * void resolveFusion(List&lt;AbstractCard&gt;, String glyph)
 * void resolveSplit(AbstractCard)
 * </pre>
 */
public final class PilotBridge {

    private PilotBridge() {
    }

    /** 一张牌能不能当合字材料。成品牌按配方展开，部件牌算一个部件。 */
    public static boolean canFuse(AbstractCard card) {
        return card != null && CardComponents.isFusable(card);
    }

    /** 这一组材料能合出哪些字（按配方表顺序，已过滤掉没有卡牌的字）。 */
    public static List<String> fusionResults(List<AbstractCard> cards) {
        List<String> glyphs = new ArrayList<String>();
        if (cards == null || cards.size() < 2) {
            return glyphs;
        }
        List<Component> parts = CardComponents.expandAll(cards);
        for (WordSpec spec : ZixiuLexicon.optionsFor(parts)) {
            if (CardComponents.create(spec.glyph()) != null) {
                glyphs.add(spec.glyph());
            }
        }
        return glyphs;
    }

    /** 一张牌能不能拆。 */
    public static boolean canSplit(AbstractCard card) {
        return HandActions.canSplit(card);
    }

    /** 拆出来的部件（枚举名，顺序同配方）。 */
    public static List<String> splitPartNames(AbstractCard card) {
        List<String> names = new ArrayList<String>();
        String glyph = CardComponents.glyphOf(card);
        if (glyph == null) {
            return names;
        }
        for (Component component : ZixiuLexicon.partsOf(glyph)) {
            names.add(component.name());
        }
        return names;
    }

    /** 本回合合出来的牌（墨迹未干），当回合不能拆。 */
    public static boolean isFresh(AbstractCard card) {
        return card != null && FusionTracker.isFresh(card);
    }

    public static int splitsThisTurn() {
        return FusionTracker.splitsThisTurn();
    }

    public static int fusionsThisTurn() {
        return FusionTracker.fusionsThisTurn();
    }

    /**
     * 同步合字：消耗这些材料，把结果放进手牌，并按正常流程触发遗物/能力/计数。
     * 调用方负责先用 {@link #fusionResults} 过滤出合法组合。
     */
    public static void resolveFusion(List<AbstractCard> cards, String glyph) {
        if (cards == null || cards.size() < 2 || glyph == null || !fusionResults(cards).contains(glyph)) {
            return;
        }
        AbstractCard result = CardComponents.create(glyph);
        if (result == null) {
            return;
        }
        for (AbstractCard card : new ArrayList<AbstractCard>(cards)) {
            AbstractDungeon.player.hand.moveToExhaustPile(card);
        }
        AbstractDungeon.player.hand.refreshHandLayout();

        AbstractCard copy = result.makeStatEquivalentCopy();
        FusionTracker.markFresh(copy);
        AbstractDungeon.player.hand.addToHand(copy);

        HandActions.notifyFusion();
        AbstractPower he = AbstractDungeon.player.getPower(HePower.POWER_ID);
        if (he instanceof HePower) {
            ((HePower) he).onFusion();
        }
    }

    /**
     * 同步拆字：原牌进消耗堆，部件直接进手牌，并触发拆解相关的能力。
     */
    public static void resolveSplit(AbstractCard card) {
        String glyph = CardComponents.glyphOf(card);
        if (glyph == null || FusionTracker.isFresh(card)) {
            return;
        }
        List<Component> parts = ZixiuLexicon.partsOf(glyph);
        if (parts.isEmpty()) {
            return;
        }
        AbstractDungeon.player.hand.moveToExhaustPile(card);
        AbstractDungeon.player.hand.refreshHandLayout();
        for (Component part : parts) {
            AbstractCard component = CardComponents.createComponent(part);
            if (component == null) {
                continue;
            }
            AbstractDungeon.player.hand.addToHand(component);
        }
        HandActions.notifySplit();
    }
}
