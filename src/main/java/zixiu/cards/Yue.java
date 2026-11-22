package zixiu.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import zixiu.lexicon.Component;

public class Yue extends ComponentCard {

    public static final String ID = "Yue";

    public Yue() {
        super(ID, Component.YUE, CardType.SKILL, CardTarget.SELF, 0, 1, 1);
    }

    @Override
    public AbstractCard makeCopy() {
        return new Yue();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
        }
    }
}
