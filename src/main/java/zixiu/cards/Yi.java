package zixiu.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import zixiu.lexicon.Component;

public class Yi extends ComponentCard {

    public static final String ID = "Yi";

    public Yi() {
        super(ID, Component.YI, CardType.SKILL, CardTarget.SELF, 0, 0, 1);
    }

    @Override
    public AbstractCard makeCopy() {
        return new Yi();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
        }
    }
}
