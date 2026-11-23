package zixiu.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import zixiu.powers.ExposurePower;

/** 汨：江面反照。 */
public class Mi extends ZixiuCard {

    public static final String ID = "Mi";
    public static final String GLYPH = "汨";

    public Mi() {
        super(ID, 1, CardType.SKILL, CardRarity.COMMON, CardTarget.ENEMY);
        this.baseMagicNumber = 3;
        this.magicNumber = 3;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        ExposurePower.apply(m, this.magicNumber);
    }

    @Override
    public AbstractCard makeCopy() {
        return new Mi();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
        }
    }
}
