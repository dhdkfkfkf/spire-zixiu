package zixiu.core;

import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 * 墨迹未干：本回合由拼合生成的牌，当回合不能拆解。
 * 用对象身份记录，回合开始时清空。
 */
public final class FusionTracker {

    private static final Set<AbstractCard> FRESH =
            Collections.newSetFromMap(new IdentityHashMap<AbstractCard, Boolean>());

    private static int splitsThisTurn = 0;
    private static int fusionsThisTurn = 0;
    /** 本次战斗累计（因的效果按这个算，回合切换不清）。 */
    private static int fusionsThisCombat = 0;

    private static int chain = 0;
    private static java.util.Set<zixiu.lexicon.Component> lastWordParts = null;

    private FusionTracker() {
    }

    public static void markFresh(AbstractCard card) {
        FRESH.add(card);
    }

    public static boolean isFresh(AbstractCard card) {
        return FRESH.contains(card);
    }

    /** 当前「墨迹未干」的牌，按对象身份返回副本（求解器镜像用）。 */
    public static Set<AbstractCard> freshCards() {
        Set<AbstractCard> copy =
                Collections.newSetFromMap(new IdentityHashMap<AbstractCard, Boolean>());
        copy.addAll(FRESH);
        return copy;
    }

    public static void recordSplit() {
        splitsThisTurn++;
    }

    public static int splitsThisTurn() {
        return splitsThisTurn;
    }

    public static void recordFusion() {
        fusionsThisTurn++;
        fusionsThisCombat++;
    }

    public static int fusionsThisTurn() {
        return fusionsThisTurn;
    }

    /** 本次战斗累计合字次数（回合结束不清，战斗开始清）。 */
    public static int fusionsThisCombat() {
        return fusionsThisCombat;
    }

    /** 战斗开始时清空累计值。 */
    public static void resetCombat() {
        fusionsThisCombat = 0;
        chain = 0;
        lastWordParts = null;
    }

    /**
     * 连书：本回合打出的这张成品牌，与上一张成品牌是否共享部件。
     * 共享则连书 +1，否则归零。返回本次之后的连书数。
     */
    public static int recordWord(java.util.Collection<zixiu.lexicon.Component> parts) {
        java.util.Set<zixiu.lexicon.Component> current =
                java.util.EnumSet.noneOf(zixiu.lexicon.Component.class);
        if (parts != null) {
            current.addAll(parts);
        }
        boolean shares = false;
        if (lastWordParts != null) {
            for (zixiu.lexicon.Component part : current) {
                if (lastWordParts.contains(part)) {
                    shares = true;
                    break;
                }
            }
        }
        chain = shares ? chain + 1 : 0;
        lastWordParts = current;
        return chain;
    }

    public static int chain() {
        return chain;
    }

    /** 上一张成品牌的部件集合（连书判定用），只读副本。 */
    public static java.util.Set<zixiu.lexicon.Component> lastWordParts() {
        java.util.Set<zixiu.lexicon.Component> copy =
                java.util.EnumSet.noneOf(zixiu.lexicon.Component.class);
        if (lastWordParts != null) {
            copy.addAll(lastWordParts);
        }
        return copy;
    }

    /** 求解器镜像写回：把这些状态一次性恢复（fresh 按对象身份存）。 */
    public static void restoreCombat(int fusionsThisCombatValue) {
        fusionsThisCombat = fusionsThisCombatValue;
    }

    public static void restore(int splits, int fusions, int newChain,
                               java.util.Collection<zixiu.lexicon.Component> lastParts,
                               java.util.Collection<AbstractCard> freshCards) {
        splitsThisTurn = splits;
        fusionsThisTurn = fusions;
        chain = newChain;
        if (lastParts == null || lastParts.isEmpty()) {
            lastWordParts = null;
        } else {
            java.util.Set<zixiu.lexicon.Component> copy =
                    java.util.EnumSet.noneOf(zixiu.lexicon.Component.class);
            copy.addAll(lastParts);
            lastWordParts = copy;
        }
        FRESH.clear();
        if (freshCards != null) {
            FRESH.addAll(freshCards);
        }
    }

    public static void clear() {
        FRESH.clear();
        splitsThisTurn = 0;
        fusionsThisTurn = 0;
        // 连书（chain / lastWordParts）**不在这里清**：它可以跨回合续上，
        // 只有战斗开始（resetCombat）才归零。
    }
}
