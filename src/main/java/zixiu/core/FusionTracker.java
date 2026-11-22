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

    private FusionTracker() {
    }

    public static void markFresh(AbstractCard card) {
        FRESH.add(card);
    }

    public static boolean isFresh(AbstractCard card) {
        return FRESH.contains(card);
    }

    public static void recordSplit() {
        splitsThisTurn++;
    }

    public static int splitsThisTurn() {
        return splitsThisTurn;
    }

    public static void recordFusion() {
        fusionsThisTurn++;
    }

    public static int fusionsThisTurn() {
        return fusionsThisTurn;
    }

    public static void clear() {
        FRESH.clear();
        splitsThisTurn = 0;
        fusionsThisTurn = 0;
    }
}
