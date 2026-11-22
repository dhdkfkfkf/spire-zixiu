package zixiu.cards;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import zixiu.lexicon.Component;

public class Thicket extends ZixiuCard {

    public static final String ID = "Thicket";
    public static final String GLYPH = "丛";

    private static final Component[] POOL = {Component.REN, Component.KOU, Component.MU, Component.HUO};

    public Thicket() {
        super(ID, 2, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.SELF);
        this.baseBlock = 4;
        this.baseMagicNumber = 2;
        this.magicNumber = 2;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p, p, this.block));
        for (int i = 0; i < this.magicNumber; i++) {
            Component component = POOL[AbstractDungeon.cardRandomRng.random(POOL.length - 1)];
            AbstractCard card = CardComponents.createComponent(component);
            if (card != null) {
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(card, false));
            }
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Thicket();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBlock(3);
        }
    }
}
