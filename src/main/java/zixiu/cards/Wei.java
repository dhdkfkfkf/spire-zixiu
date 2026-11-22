package zixiu.cards;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import zixiu.powers.ExposurePower;

/**
 * 未：来日未至，先照个透。
 */
public class Wei extends ZixiuCard {

    public static final String ID = "Wei";
    public static final String GLYPH = "未";

    public Wei() {
        super(ID, 1, CardType.SKILL, CardRarity.COMMON, CardTarget.ENEMY);
        this.baseMagicNumber = 1;
        this.magicNumber = 1;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        ExposurePower.apply(m, this.magicNumber);
        AbstractDungeon.actionManager.addToBottom(new DrawCardAction(p, 2));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Wei();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
        }
    }
}
