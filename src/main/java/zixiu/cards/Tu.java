package zixiu.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import zixiu.lexicon.Component;

public class Tu extends ComponentCard {

    public static final String ID = "Tu";

    public Tu() {
        super(ID, Component.TU, CardType.SKILL, CardTarget.SELF, 0, 3, 0);
    }

    @Override
    public AbstractCard makeCopy() {
        return new Tu();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBlock(1);
        }
    }
}
