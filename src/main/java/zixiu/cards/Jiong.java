package zixiu.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import zixiu.powers.BurnPower;
import zixiu.powers.ExposurePower;
import zixiu.powers.JiongPower;

public class Jiong extends ZixiuCard {

    public static final String ID = "Jiong";
    public static final String GLYPH = "炅";

    public Jiong() {
        super(ID, 1, CardType.SKILL, CardRarity.RARE, CardTarget.ENEMY);
        this.baseMagicNumber = 3;
        this.magicNumber = 3;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        ExposurePower.apply(m, this.magicNumber);
        if (m.hasPower(BurnPower.POWER_ID)) {
            AbstractDungeon.actionManager.addToBottom(new DrawCardAction(p, 2));
        }
        AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(p, p, new JiongPower(p), 1));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Jiong();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
        }
    }
}
