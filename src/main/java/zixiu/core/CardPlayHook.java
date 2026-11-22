package zixiu.core;

import basemod.interfaces.OnCardUseSubscriber;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import zixiu.cards.CardComponents;
import zixiu.powers.CausePower;
import zixiu.powers.OsmanthusPower;

/**
 * 打出卡牌时的全局钩子：桂（技能牌）与因（成品牌）。
 */
public class CardPlayHook implements OnCardUseSubscriber {

    @Override
    public void receiveCardUsed(AbstractCard card) {
        if (AbstractDungeon.player == null) {
            return;
        }
        if (card.type == AbstractCard.CardType.SKILL) {
            AbstractPower gui = AbstractDungeon.player.getPower(OsmanthusPower.POWER_ID);
            if (gui instanceof OsmanthusPower) {
                ((OsmanthusPower) gui).onSkillPlayed();
            }
        }
        if (CardComponents.glyphOf(card) != null) {
            AbstractPower yin = AbstractDungeon.player.getPower(CausePower.POWER_ID);
            if (yin instanceof CausePower) {
                ((CausePower) yin).onWordPlayed();
            }
        }
    }
}
