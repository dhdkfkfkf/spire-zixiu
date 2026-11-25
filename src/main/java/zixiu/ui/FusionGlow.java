package zixiu.ui;

import basemod.interfaces.PostUpdateSubscriber;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zixiu.cards.CardComponents;
import zixiu.lexicon.Component;
import zixiu.lexicon.WordSpec;
import zixiu.lexicon.ZixiuLexicon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

/**
 * 合字选牌提示：选牌界面里已经放进至少一张材料时，还能跟它合出结果的部件牌外沿冒金光。
 *
 * 用的就是原版那套接口：{@link AbstractCard#beginGlowing()} / {@link AbstractCard#stopGlowing()}
 * 加 {@code glowColor}。卡牌每次渲染都会走 {@code updateGlow()}，只要 isGlowing 为真，
 * 就每 0.3 秒生成一个 {@code CardGlowBorder}——手牌边缘那圈浅蓝光、遗物提示的金光都是这个机制。
 * 所以这里只负责按帧维护「该亮的牌」和它们的颜色。
 */
public class FusionGlow implements PostUpdateSubscriber {

    private static final Logger logger = LogManager.getLogger(FusionGlow.class);

    /** 原版的 GOLD_BORDER_GLOW_COLOR 就是 Color.GOLD，直接用同一个黄。 */
    private static final Color GOLD = new Color(Color.GOLD);

    /** 我们自己点亮的牌，以及它们原来的 glowColor（退出时还原，否则金光是永久变色）。 */
    private final Set<AbstractCard> lit =
            Collections.newSetFromMap(new IdentityHashMap<AbstractCard, Boolean>());
    private final java.util.Map<AbstractCard, Color> savedGlow =
            new IdentityHashMap<AbstractCard, Color>();

    @Override
    public void receivePostUpdate() {
        HandActions.tickSelectScreen();
        Set<AbstractCard> want = candidates();
        for (AbstractCard card : new ArrayList<AbstractCard>(lit)) {
            if (!want.contains(card)) {
                stopGlow(card);
            }
        }
        for (AbstractCard card : want) {
            // 按 isGlowing 判断而不是「有没有点过灯」：界面上有东西会把 isGlowing 抹掉，
            // 抹掉后如果只看 lit 就再也不会重新点亮（表现为冒一下就消失，得重新选牌）。
            if (!savedGlow.containsKey(card)) {
                savedGlow.put(card, new Color(card.glowColor));
            }
            card.glowColor = new Color(GOLD);
            if (!card.isGlowing) {
                card.beginGlowing();
                if (!lit.contains(card)) {
                    logger.info("[zixiu] fusion glow on: " + card.cardID);
                }
            }
            lit.add(card);
        }
    }

    /** 熄掉自己点的那盏灯，并把 glowColor 还原成原来的（原版那圈浅蓝光还要用）。 */
    private void stopGlow(AbstractCard card) {
        Color previous = savedGlow.remove(card);
        if (previous != null) {
            card.glowColor = previous;
        }
        card.stopGlowing();
        lit.remove(card);
    }

    /** 当前还能跟已选材料合出字（而且那个字有卡）的部件牌。 */
    private static Set<AbstractCard> candidates() {
        Set<AbstractCard> none = Collections.emptySet();
        if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.HAND_SELECT
                || !HandActions.fusing
                || AbstractDungeon.player == null
                || AbstractDungeon.handCardSelectScreen == null) {
            return none;
        }
        List<AbstractCard> selected = AbstractDungeon.handCardSelectScreen.selectedCards.group;
        // 一张都没放，或者已经放满：谈不上「还能跟它合」
        if (selected.isEmpty() || selected.size() >= HandActions.MAX_FUSION_CARDS) {
            return none;
        }
        List<Component> parts = CardComponents.expandAll(selected);
        Set<AbstractCard> out = Collections.newSetFromMap(new IdentityHashMap<AbstractCard, Boolean>());
        for (AbstractCard card : AbstractDungeon.player.hand.group) {
            if (!CardComponents.isFusable(card)) {
                continue;
            }
            List<Component> combined = new ArrayList<Component>(parts);
            combined.addAll(CardComponents.expand(card));
            for (WordSpec spec : ZixiuLexicon.optionsFor(combined)) {
                if (CardComponents.isImplemented(spec.glyph())) {
                    out.add(card);
                    break;
                }
            }
        }
        return out;
    }
}
