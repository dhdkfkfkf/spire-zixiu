package zixiu.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import zixiu.lexicon.Component;

public class Mu extends ComponentCard {

    public static final String ID = "Mu";

    public Mu() {
        super(ID, Component.MU, CardType.SKILL, CardTarget.SELF, 0, 2, 0);
    }

    @Override
    public AbstractCard makeCopy() {
        return new Mu();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBlock(1);
        }
    }
}
