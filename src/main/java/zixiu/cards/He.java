package zixiu.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import zixiu.powers.HePower;

/**
 * 合：合字本身就是收益。每合出一个字，摸一张、挡一下。
 */
public class He extends ZixiuCard {

    public static final String ID = "He";
    public static final String GLYPH = "合";

    public He() {
        super(ID, 2, CardType.POWER, CardRarity.RARE, CardTarget.SELF);
        this.baseMagicNumber = 2;
        this.magicNumber = 2;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new HePower(p), 1));
    }

    @Override
    public AbstractCard makeCopy() {
        return new He();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.isInnate = true;
            String upgradeDesc = strings(ID).UPGRADE_DESCRIPTION;
            if (upgradeDesc != null) {
                this.rawDescription = upgradeDesc;
                initializeDescription();
            }
        }
    }
}
