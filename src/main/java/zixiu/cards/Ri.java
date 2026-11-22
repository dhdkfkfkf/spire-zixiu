package zixiu.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import zixiu.lexicon.Component;

public class Ri extends ComponentCard {

    public static final String ID = "Ri";

    public Ri() {
        super(ID, Component.RI, CardType.SKILL, CardTarget.ENEMY, 0, 0, 1);
    }

    @Override
    public AbstractCard makeCopy() {
        return new Ri();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
        }
    }
}
