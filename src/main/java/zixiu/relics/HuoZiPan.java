package zixiu.relics;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import zixiu.ZixiuMod;
import zixiu.cards.CardComponents;
import zixiu.lexicon.Component;

/**
 * 活字盘：每回合开始时，将 1 张随机部件牌加入手牌。
 */
public class HuoZiPan extends ZixiuRelic {

    public static final String ID = "HuoZiPan";


    public HuoZiPan() {
        super(ZixiuMod.makeID(ID), "huozipan.png", RelicTier.RARE, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void atTurnStartPostDraw() {
        Component component = Component.values()[AbstractDungeon.cardRandomRng.random(Component.values().length - 1)];
        AbstractCard card = CardComponents.createComponent(component);
        if (card != null) {
            this.flash();
            AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(card, false));
        }
    }

    @Override
    public AbstractRelic makeCopy() {
        return new HuoZiPan();
    }
}
