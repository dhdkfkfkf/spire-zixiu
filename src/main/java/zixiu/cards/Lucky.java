package zixiu.cards;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import zixiu.lexicon.Component;

public class Lucky extends ZixiuCard {

    public static final String ID = "Lucky";
    public static final String GLYPH = "吉";

    private static final Component[] POOL = {Component.REN, Component.KOU, Component.MU, Component.HUO};

    public Lucky() {
        super(ID, 1, CardType.SKILL, CardRarity.COMMON, CardTarget.SELF);
        this.baseMagicNumber = 1;
        this.magicNumber = 1;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        for (int i = 0; i < this.magicNumber; i++) {
            Component component = POOL[AbstractDungeon.cardRandomRng.random(POOL.length - 1)];
            AbstractCard card = CardComponents.createComponent(component);
            if (card != null) {
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(card, false));
            }
        }
        AbstractDungeon.actionManager.addToBottom(new DrawCardAction(p, 1));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Lucky();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
        }
    }
}
