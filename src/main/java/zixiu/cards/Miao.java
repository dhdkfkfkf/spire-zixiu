package zixiu.cards;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import zixiu.powers.BurnPower;
import zixiu.powers.ExposurePower;

/**
 * 淼：水面反光。把火的余光摊到整片水上。
 *
 * 淼的配方是三水：拆得开，也合得回来（水+水+水）。
 */
public class Miao extends ZixiuCard {

    public static final String ID = "Miao";
    public static final String GLYPH = "淼";

    public Miao() {
        super(ID, 1, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.ENEMY);
        this.baseMagicNumber = 3;
        this.magicNumber = 3;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        ExposurePower.apply(m, this.magicNumber);
        if (m != null && m.hasPower(BurnPower.POWER_ID)) {
            AbstractDungeon.actionManager.addToBottom(new DrawCardAction(p, 2));
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Miao();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(2);
        }
    }
}
