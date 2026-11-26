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

    // ---- 「未」选牌的握手 ----

    /**
     * 求解器实时选牌的握手标志。
     *
     * 求解器替玩家填「未」的网格选择时把它置 true、答完再清掉；置 true 时
     * {@code PlayFromDrawPileAction} 会**自己取走选择并关界面**，不再等界面关闭——
     * 否则两边互等（求解器等我们消费、我们等界面关掉）就会卡在选牌界面。
     *
     * 对照：送葬人的 BetterSelectCardsAction 用「这次选择是不是玩家点的」来判断，
     * 我们用这个显式标志，触摸端也不用依赖 hover 状态。
     */
    private static boolean solverSelecting;

    public static boolean solverSelecting() {
        return solverSelecting;
    }

    public static void setSolverSelecting(boolean value) {
        solverSelecting = value;
    }

    /** 当前连书数（没有连书遗物时也照实返回，链是否生效看 {@link #chainRelic()}）。 */
    public static int chain() {
        return FusionTracker.chain();
    }

    /** 是否带着连书遗物（连书接链的前提）。 */
    public static boolean chainRelic() {
        return AbstractDungeon.player != null
                && AbstractDungeon.player.hasRelic(zixiu.ZixiuMod.makeID(zixiu.relics.LianShu.ID));
    }

    /** 「沓」还欠几次曝光翻倍。 */
    public static int taCharges() {
        if (AbstractDungeon.player == null) {
            return 0;
        }
        AbstractPower ta = AbstractDungeon.player.getPower(zixiu.powers.TaPower.POWER_ID);
        return ta == null ? 0 : ta.amount;
    }

    /** 「沓」的翻倍窗口是否正开着（正在结算那张攻击牌）。 */
    public static boolean taWindowOpen() {
        return zixiu.powers.TaPower.windowOpen();
    }

    /** 言灵指针 1..9，没有这个能力时返回 0。 */
    public static int yanlingIndex() {
        return zixiu.powers.YanlingPower.index(AbstractDungeon.player);
    }

    /** 言灵「在」的减费欠账。 */
    public static boolean yanlingDiscountPending() {
        return zixiu.powers.YanlingPower.discountPending();
    }

    /** 本次战斗累计合字次数（「因」的伤害基数）。 */
    public static int fusionsThisCombat() {
        return FusionTracker.fusionsThisCombat();
    }

    /** 拆合规则方案：1 = 产出虚无/材料保留，2 = 产出保留/材料消耗。 */
    public static int fusionMode() {
        return zixiu.core.ZixiuConfig.fusionMode();
    }

    /** 快照全部需要镜像的静态量；{@link zixiu.compat.PilotStatics#restore()} 写回。 */
    public static zixiu.compat.PilotStatics captureStatics() {
        return zixiu.compat.PilotStatics.capture();
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

    /** 材料去处：方案 2 进消耗堆，方案 1 进弃牌堆（与 HandActions.consumeCard 一致）。 */
    private static void moveMaterial(AbstractCard card) {
        if (zixiu.core.ZixiuConfig.exhaustsMaterials()) {
            AbstractDungeon.player.hand.moveToExhaustPile(card);
        } else {
            AbstractDungeon.player.hand.moveToDiscardPile(card);
        }
    }

    /**
     * 同步合字：和 {@link HandActions#executeFusion} 完全同口径——
     * 材料去处看 {@link zixiu.core.ZixiuConfig#exhaustsMaterials()}（消耗堆 / 弃牌堆），
     * 产出是否虚无+消耗看 {@link zixiu.core.ZixiuConfig#transientResults()}，
     * 升级按「升级材料数 ≥ 未升级材料数」传递。
     *
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
        boolean upgradeResult = HandActions.shouldUpgradeResult(cards);
        for (AbstractCard card : new ArrayList<AbstractCard>(cards)) {
            moveMaterial(card);
        }
        AbstractDungeon.player.hand.refreshHandLayout();

        if (upgradeResult) {
            HandActions.upgradeIfPossible(result);
        }
        AbstractCard copy = result.makeStatEquivalentCopy();
        FusionTracker.markFresh(copy);
        if (zixiu.core.ZixiuConfig.transientResults()) {
            zixiu.cards.CardComponents.markTransient(copy);
        }
        AbstractDungeon.player.hand.addToHand(copy);

        // notifyFusion() 里已经含「合」的触发（HePower），不要再单独调一次
        HandActions.notifyFusion();
    }

    /**
     * 同步拆字：口径同 {@link HandActions#executeSplit}——
     * 源牌去处看方案（消耗堆 / 弃牌堆），部件继承源牌的升级状态，产出是否虚无+消耗也看方案。
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
        boolean upgraded = card.upgraded;
        moveMaterial(card);
        AbstractDungeon.player.hand.refreshHandLayout();
        for (Component part : parts) {
            AbstractCard component = CardComponents.createComponent(part);
            if (component == null) {
                continue;
            }
            if (upgraded) {
                HandActions.upgradeIfPossible(component);
            }
            if (zixiu.core.ZixiuConfig.transientResults()) {
                zixiu.cards.CardComponents.markTransient(component);
            }
            AbstractDungeon.player.hand.addToHand(component);
        }
        HandActions.notifySplit();
    }
}
