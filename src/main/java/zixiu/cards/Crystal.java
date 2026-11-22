package zixiu.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import zixiu.powers.ExposurePower;

public class Crystal extends ZixiuCard {

    public static final String ID = "Crystal";
    public static final String GLYPH = "晶";

    public Crystal() {
        super(ID, 1, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.ENEMY);
        this.baseMagicNumber = 1;
        this.magicNumber = 1;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractPower exposure = m.getPower(ExposurePower.POWER_ID);
        if (exposure != null) {
            exposure.amount *= 2;
            exposure.updateDescription();
            exposure.flash();
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Crystal();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBaseCost(0);
        }
    }
}
