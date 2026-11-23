package zixiu.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import zixiu.powers.WholePower;

public class Whole extends ZixiuCard {

    public static final String ID = "Whole";
    public static final String GLYPH = "全";

    public Whole() {
        super(ID, 2, CardType.POWER, CardRarity.RARE, CardTarget.SELF);
        this.baseMagicNumber = 1;
        this.magicNumber = 1;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(p, p, new WholePower(p), 1));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Whole();
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
