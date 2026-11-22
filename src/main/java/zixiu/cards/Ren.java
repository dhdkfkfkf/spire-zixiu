package zixiu.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import zixiu.lexicon.Component;

public class Ren extends ComponentCard {

    public static final String ID = "Ren";

    public Ren() {
        super(ID, Component.REN, CardType.ATTACK, CardTarget.ENEMY, 1, 0, 2);
    }

    @Override
    public AbstractCard makeCopy() {
        return new Ren();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
        }
    }
}
