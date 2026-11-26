package zixiu.relics;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import zixiu.ZixiuMod;
import zixiu.cards.CardComponents;
import zixiu.cards.ComponentCard;
import zixiu.lexicon.Component;

/**
 * 旧书签：回合开始时，若手牌中没有部件牌，将 1 张随机部件牌加入手牌。
 */
public class JiuShuQian extends ZixiuRelic {

    public static final String ID = "JiuShuQian";


    public JiuShuQian() {
        super(ZixiuMod.makeID(ID), "jiushuqian.png", RelicTier.COMMON, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void atTurnStartPostDraw() {
        for (AbstractCard card : AbstractDungeon.player.hand.group) {
            if (card instanceof ComponentCard) {
                return;
            }
        }
        Component component = Component.values()[AbstractDungeon.cardRandomRng.random(Component.values().length - 1)];
        AbstractCard card = CardComponents.createComponent(component);
        if (card != null) {
            this.flash();
            AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(card, false));
        }
    }

    @Override
    public AbstractRelic makeCopy() {
        return new JiuShuQian();
    }
}
