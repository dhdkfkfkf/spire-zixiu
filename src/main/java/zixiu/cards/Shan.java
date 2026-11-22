package zixiu.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import zixiu.lexicon.Component;

public class Shan extends ComponentCard {

    public static final String ID = "Shan";

    public Shan() {
        super(ID, Component.SHAN, CardType.SKILL, CardTarget.ENEMY, 0, 0, 1);
    }

    @Override
    public AbstractCard makeCopy() {
        return new Shan();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
        }
    }
}
