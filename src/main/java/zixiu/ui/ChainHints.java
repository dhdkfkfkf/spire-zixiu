package zixiu.ui;

import basemod.interfaces.PostUpdateSubscriber;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import zixiu.ZixiuMod;
import zixiu.cards.CardComponents;
import zixiu.core.FusionTracker;
import zixiu.lexicon.Component;
import zixiu.lexicon.ZixiuLexicon;
import zixiu.relics.LianShu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 连书提示：手牌里还能接上连书的成品牌外沿冒金光（和合字选牌界面同一套 glow 接口）。
 *
 * 「上一字：森（木，木，木）」在血条下的连书 buff 里，见 {@link zixiu.powers.ChainPower}。
 * 只在正常战斗界面里工作：合字/拆字选牌界面由 {@link FusionGlow} 负责，两边不会抢同一张牌。
 */
public class ChainHints implements PostUpdateSubscriber {

    /** 与 FusionGlow 用同一个金。 */
    private static final Color GOLD = new Color(Color.GOLD);

    /** 我们点亮的牌，以及它们原来的 glowColor（退出时还原）。 */
    private final Set<AbstractCard> lit =
            Collections.newSetFromMap(new IdentityHashMap<AbstractCard, Boolean>());
    private final Map<AbstractCard, Color> savedGlow = new IdentityHashMap<AbstractCard, Color>();

    @Override
    public void receivePostUpdate() {
        if (!HandActions.inCombat()) {
            stopAll();
            return;
        }
        Set<AbstractCard> want = candidates();
        for (AbstractCard card : new ArrayList<AbstractCard>(lit)) {
            if (!want.contains(card)) {
                stopGlow(card);
            }
        }
        for (AbstractCard card : want) {
            // 和 FusionGlow 一样按 isGlowing 判断：界面上有东西会把它抹掉，抹掉后要重新点亮
            if (!savedGlow.containsKey(card)) {
                savedGlow.put(card, new Color(card.glowColor));
            }
            card.glowColor = new Color(GOLD);
            if (!card.isGlowing) {
                card.beginGlowing();
            }
            lit.add(card);
        }
    }

    /** 手牌里能接上连书的成品牌：与上一张成品牌至少共享一个部件。 */
    private static Set<AbstractCard> candidates() {
        Set<AbstractCard> none = Collections.emptySet();
        if (AbstractDungeon.player == null) {
            return none;
        }
        if (!AbstractDungeon.player.hasRelic(ZixiuMod.makeID(LianShu.ID))) {
            return none;
        }
        Set<Component> last = FusionTracker.lastWordParts();
        if (last.isEmpty()) {
            return none;
        }
        Set<AbstractCard> out =
                Collections.newSetFromMap(new IdentityHashMap<AbstractCard, Boolean>());
        for (AbstractCard card : AbstractDungeon.player.hand.group) {
            String glyph = CardComponents.glyphOf(card);
            if (glyph == null) {
                continue;
            }
            for (Component part : ZixiuLexicon.partsOf(glyph)) {
                if (last.contains(part)) {
                    out.add(card);
                    break;
                }
            }
        }
        return out;
    }

    private void stopAll() {
        for (AbstractCard card : new ArrayList<AbstractCard>(lit)) {
            stopGlow(card);
        }
    }

    /** 熄灯并还原 glowColor（原版那圈浅蓝光还要用）。 */
    private void stopGlow(AbstractCard card) {
        Color previous = savedGlow.remove(card);
        if (previous != null) {
            card.glowColor = previous;
        }
        card.stopGlowing();
        lit.remove(card);
    }
}
