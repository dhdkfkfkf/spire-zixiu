package zixiu.relics;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import zixiu.ZixiuMod;
import zixiu.cards.CardComponents;
import zixiu.lexicon.Component;

/**
 * 书匣：每当你拆解一张牌，将 1 张随机部件牌加入手牌（每回合上限 1）。
 */
public class ShuXia extends ZixiuRelic {

    public static final String ID = "ShuXia";


    private boolean usedThisTurn = false;

    public ShuXia() {
        super(ZixiuMod.makeID(ID), "shuxia.png", RelicTier.UNCOMMON, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void atTurnStart() {
        this.usedThisTurn = false;
    }

    public void onSplit() {
        if (this.usedThisTurn) {
            return;
        }
        this.usedThisTurn = true;
        Component component = Component.values()[AbstractDungeon.cardRandomRng.random(Component.values().length - 1)];
        AbstractCard card = CardComponents.createComponent(component);
        if (card != null) {
            this.flash();
            AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(card, false));
        }
    }

    @Override
    public AbstractRelic makeCopy() {
        return new ShuXia();
    }
}
