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

    public static void clear() {
        FRESH.clear();
        splitsThisTurn = 0;
        fusionsThisTurn = 0;
        chain = 0;
        lastWordParts = null;
    }
}
