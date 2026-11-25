package zixiu.compat;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import zixiu.core.FusionTracker;
import zixiu.lexicon.Component;
import zixiu.powers.TaPower;
import zixiu.powers.YanlingPower;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 * 求解器镜像用的静态量快照。
 *
 * 原来求解器要反射进 {@code FusionTracker.FRESH} / {@code TaPower.windowOpen} 这些私有静态字段，
 * 既脆又容易漏。现在统一成一对调用：
 *
 * <pre>
 * Object before = PilotStatics.capture();   // 进沙盒前
 * ...推演...
 * ((PilotStatics) before).restore();        // 出沙盒后还原
 * </pre>
 *
 * 需要注意：{@link #fresh} 按**对象身份**保存（IdentityHashMap 语义），还原时不能按 cardId 去重。
 */
public final class PilotStatics {

    public final int splitsThisTurn;
    public final int fusionsThisTurn;
    /** 本次战斗累计合字次数（因的伤害基数）。 */
    public final int fusionsThisCombat;
    /** 拆合规则方案：1 = 产出虚无/材料保留，2 = 产出保留/材料消耗。 */
    public final int fusionMode;
    public final int chain;
    public final Set<Component> lastWordParts;
    /** 上一张成品牌是哪个字（只影响连书遗物的显示文字）。 */
    public final String lastWordGlyph;
    public final Set<AbstractCard> fresh;
    public final boolean taWindowOpen;
    public final int yanlingIndex;
    public final boolean yanlingDiscountPending;

    private PilotStatics(int splitsThisTurn, int fusionsThisTurn, int fusionsThisCombat,
                         int fusionMode, int chain,
                         Set<Component> lastWordParts, String lastWordGlyph,
                         Set<AbstractCard> fresh,
                         boolean taWindowOpen, int yanlingIndex,
                         boolean yanlingDiscountPending) {
        this.splitsThisTurn = splitsThisTurn;
        this.fusionsThisTurn = fusionsThisTurn;
        this.fusionsThisCombat = fusionsThisCombat;
        this.fusionMode = fusionMode;
        this.chain = chain;
        this.lastWordParts = lastWordParts;
        this.lastWordGlyph = lastWordGlyph;
        this.fresh = fresh;
        this.taWindowOpen = taWindowOpen;
        this.yanlingIndex = yanlingIndex;
        this.yanlingDiscountPending = yanlingDiscountPending;
    }

    public static PilotStatics capture() {
        Set<Component> parts = EnumSet.noneOf(Component.class);
        parts.addAll(FusionTracker.lastWordParts());

        Set<AbstractCard> fresh = FusionTracker.freshCards();

        return new PilotStatics(
                FusionTracker.splitsThisTurn(),
                FusionTracker.fusionsThisTurn(),
                FusionTracker.fusionsThisCombat(),
                zixiu.core.ZixiuConfig.fusionMode(),
                FusionTracker.chain(),
                parts,
                FusionTracker.lastWordGlyph(),
                fresh,
                TaPower.windowOpen(),
                YanlingPower.index(AbstractDungeon.player),
                YanlingPower.discountPending());
    }

    /** 把快照写回当前世界。 */
    public void restore() {
        FusionTracker.restore(splitsThisTurn, fusionsThisTurn, chain, lastWordParts, fresh,
                lastWordGlyph);
        FusionTracker.restoreCombat(fusionsThisCombat);
        zixiu.core.ZixiuConfig.setFusionMode(fusionMode);
        TaPower.setWindowOpen(taWindowOpen);
        YanlingPower.setDiscountPending(yanlingDiscountPending);
        if (yanlingIndex > 0) {
            YanlingPower.setIndex(AbstractDungeon.player, yanlingIndex);
        }
    }

    @Override
    public String toString() {
        return "PilotStatics{splits=" + splitsThisTurn + ", fusions=" + fusionsThisTurn
                + ", fusionsCombat=" + fusionsThisCombat + ", mode=" + fusionMode
                + ", chain=" + chain + ", taWindow=" + taWindowOpen
                + ", yanling=" + yanlingIndex + ", discount=" + yanlingDiscountPending + '}';
    }
}
