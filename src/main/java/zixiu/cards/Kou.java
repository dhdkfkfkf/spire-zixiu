package zixiu.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import zixiu.lexicon.Component;

public class Kou extends ComponentCard {

    public static final String ID = "Kou";

    public Kou() {
        super(ID, Component.KOU, CardType.SKILL, CardTarget.SELF, 0, 0, 1);
    }

    @Override
    public AbstractCard makeCopy() {
        return new Kou();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
        }
    }
}
