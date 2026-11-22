package zixiu.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import zixiu.lexicon.Component;

public class Shui extends ComponentCard {

    public static final String ID = "Shui";

    public Shui() {
        super(ID, Component.SHUI, CardType.SKILL, CardTarget.SELF, 0, 0, 2);
    }

    @Override
    public AbstractCard makeCopy() {
        return new Shui();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
        }
    }
}
