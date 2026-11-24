package zixiu.ui;

import com.evacipated.cardcrawl.mod.stslib.actions.common.SelectCardsInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDiscardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zixiu.actions.ChooseWordAction;
import zixiu.cards.CardComponents;
import zixiu.core.FusionTracker;
import zixiu.lexicon.Component;
import zixiu.lexicon.WordSpec;
import zixiu.lexicon.ZixiuLexicon;
import zixiu.powers.HePower;
import zixiu.powers.JiongPower;
import zixiu.powers.PengPower;
import zixiu.powers.SenPower;

import java.util.ArrayList;
import java.util.List;

/**
 * 合与拆的全部业务逻辑。按钮只负责绘制和点击，规则都在这里。
 */
public final class HandActions {

    private static final Logger logger = LogManager.getLogger(HandActions.class);

    /** 当前选牌界面是拆解还是拼合，供 HandHints 决定显示哪种提示。 */
    public static boolean splitting = false;

    private HandActions() {
    }

    public static boolean inCombat() {
        if (AbstractDungeon.player == null) {
            return false;
        }
        if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.NONE) {
            return false;
        }
        if (AbstractDungeon.isScreenUp) {
            return false;
        }
        com.megacrit.cardcrawl.rooms.AbstractRoom room;
        try {
            room = AbstractDungeon.getCurrRoom();
        } catch (NullPointerException e) {
            return false;
        }
        return room != null && room.phase == com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase.COMBAT;
    }

    public static int fusableCount() {
        if (!inCombat()) {
            return 0;
        }
        int count = 0;
        for (AbstractCard card : AbstractDungeon.player.hand.group) {
            if (CardComponents.isFusable(card)) {
                count++;
            }
        }
        return count;
    }

    public static String handSummary() {
        if (AbstractDungeon.player == null) {
            return "<no player>";
        }
        StringBuilder sb = new StringBuilder();
        for (AbstractCard card : AbstractDungeon.player.hand.group) {
            sb.append(card.getClass().getSimpleName())
                    .append(':').append(card.cardID)
                    .append(' ');
        }
        return sb.toString();
    }

    /** 手上是否真的存在一对能拼出字的组合。只看「各自可拆」会有假阳性。 */
    public static boolean hasFusablePair() {
        if (!inCombat()) {
            return false;
        }
        List<AbstractCard> pool = new ArrayList<AbstractCard>();
        for (AbstractCard card : AbstractDungeon.player.hand.group) {
            if (CardComponents.isFusable(card)) {
                pool.add(card);
            }
        }
        for (int i = 0; i < pool.size(); i++) {
            for (int j = i + 1; j < pool.size(); j++) {
                List<Component> parts = new ArrayList<Component>(CardComponents.expand(pool.get(i)));
                parts.addAll(CardComponents.expand(pool.get(j)));
                if (!ZixiuLexicon.optionsFor(parts).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int splittableCount() {
        if (!inCombat()) {
            return 0;
        }
        int count = 0;
        for (AbstractCard card : AbstractDungeon.player.hand.group) {
            if (canSplit(card)) {
                count++;
            }
        }
        return count;
    }

    public static boolean hasFreshWord() {
        if (!inCombat()) {
            return false;
        }
        for (AbstractCard card : AbstractDungeon.player.hand.group) {
            if (CardComponents.glyphOf(card) != null && FusionTracker.isFresh(card)) {
                return true;
            }
        }
        return false;
    }

    public static boolean canSplit(AbstractCard card) {
        String glyph = CardComponents.glyphOf(card);
        if (glyph == null) {
            return false;
        }
        if (FusionTracker.isFresh(card)) {
            return false;
        }
        return !ZixiuLexicon.partsOf(glyph).isEmpty();
    }

    public static void openFusion() {
        splitting = false;
        // anyNumber = true：确定键一直可用，手上一张都没有时点确定就是取消；
        // canPickZero = true：强制弹出选牌界面（否则 StSLib 在可选数 <= 2 时会自动完成）。
        AbstractDungeon.actionManager.addToBottom(new SelectCardsInHandAction(
                3, "选择你想拼合的牌（最多3张）", true, true,
                CardComponents::isFusable, HandActions::executeFusion));
    }

    public static void openSplit() {
        splitting = true;
        AbstractDungeon.actionManager.addToBottom(new SelectCardsInHandAction(
                1, "拆分（选一张）", true, true,
                HandActions::canSplit, HandActions::onSplitSelected));
    }

    public static void executeFusion(List<AbstractCard> cards) {
        if (cards.size() < 2) {
            if (cards.size() == 1) {
                ScreenToast.show("至少要两张牌才能合字");
            }
            return;
        }
        List<Component> parts = CardComponents.expandAll(cards);
        List<WordSpec> options = new ArrayList<WordSpec>();
        for (WordSpec spec : ZixiuLexicon.optionsFor(parts)) {
            // 配方表是 85 字全量，但只有已经实现成卡牌的字才能选。
            if (CardComponents.create(spec.glyph()) != null) {
                options.add(spec);
            }
        }
        logger.info("[zixiu] fuse parts=" + parts + " options=" + options);
        if (options.isEmpty()) {
            ScreenToast.show("这几张牌合不出结果");
            return;
        }

        for (AbstractCard card : cards) {
            consumeCard(card);
        }
        AbstractDungeon.player.hand.refreshHandLayout();

        if (options.size() == 1) {
            AbstractCard result = CardComponents.create(options.get(0).glyph());
            if (result != null) {
                ChooseWordAction.putIntoHand(result);
            }
            notifyFusion();
            return;
        }
        AbstractDungeon.actionManager.addToTop(new ChooseWordAction(options));
        notifyFusion();
    }

    private static void onSplitSelected(List<AbstractCard> cards) {
        if (cards.size() != 1) {
            return;
        }
        executeSplit(cards.get(0));
    }

    public static void executeSplit(AbstractCard card) {
        if (card == null) {
            return;
        }
        split(card);
    }

    private static void split(AbstractCard card) {
        String glyph = CardComponents.glyphOf(card);
        if (glyph == null) {
            return;
        }
        List<Component> parts = ZixiuLexicon.partsOf(glyph);
        logger.info("[zixiu] split " + glyph + " -> " + parts);

        consumeCard(card);
        AbstractDungeon.player.hand.refreshHandLayout();
        for (Component part : parts) {
            addToHand(CardComponents.createComponent(part));
        }
        triggerSplitEffects();
    }

    /**
     * 选牌界面里的牌不在 hand.group 里，只调 moveToExhaustPile 会被界面随后还回手牌，
     * 导致同一张牌可以无限拆。必须先从 selectedCards 里摘掉。
     */
    private static void consumeCard(AbstractCard card) {
        AbstractDungeon.handCardSelectScreen.selectedCards.group.remove(card);
        AbstractDungeon.player.hand.moveToExhaustPile(card);
    }

    private static void addToHand(AbstractCard card) {
        if (card == null) {
            return;
        }
        card.current_x = -1000.0F * Settings.xScale;
        // 拆出来的部件是虚无：回合结束自动消耗；本回合不占手牌上限
        card.isEthereal = true;
        AbstractDungeon.effectList.add(new ShowCardAndAddToHandEffect(
                card, (float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
    }

    /** 合字触发：遗物 + 能力。外部（求解器）同步执行时也走这里。 */
    public static void notifyFusion() {
        zixiu.core.RelicHooks.onFusion(AbstractDungeon.player);
        triggerFusionEffects();
    }

    /** 拆字触发：遗物 + 能力 + 本回合计数。 */
    public static void notifySplit() {
        triggerSplitEffects();
    }

    private static void triggerSplitEffects() {
        FusionTracker.recordSplit();
        zixiu.core.RelicHooks.onSplit(AbstractDungeon.player);
        AbstractPower sen = AbstractDungeon.player.getPower(SenPower.POWER_ID);
        if (sen instanceof SenPower) {
            ((SenPower) sen).onSplit();
        }
        AbstractPower jiong = AbstractDungeon.player.getPower(JiongPower.POWER_ID);
        if (jiong instanceof JiongPower) {
            ((JiongPower) jiong).onSplit();
        }
        AbstractPower peng = AbstractDungeon.player.getPower(PengPower.POWER_ID);
        if (peng instanceof PengPower) {
            ((PengPower) peng).onSplit();
        }
    }

    private static void triggerFusionEffects() {
        FusionTracker.recordFusion();
        AbstractPower he = AbstractDungeon.player.getPower(HePower.POWER_ID);
        if (he instanceof HePower) {
            ((HePower) he).onFusion();
        }
    }
}
