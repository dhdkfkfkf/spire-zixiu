package zixiu.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import zixiu.lexicon.Component;

public class Huo extends ComponentCard {

    public static final String ID = "Huo";

    public Huo() {
        super(ID, Component.HUO, CardType.ATTACK, CardTarget.ENEMY, 2, 0, 0);
    }

    @Override
    public AbstractCard makeCopy() {
        return new Huo();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(1);
        }
    }
}
