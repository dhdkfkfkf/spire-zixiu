package zixiu.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import zixiu.powers.FlourishPower;

public class Prosper extends ZixiuCard {

    public static final String ID = "Prosper";
    public static final String GLYPH = "昌";

    public Prosper() {
        super(ID, 1, CardType.POWER, CardRarity.COMMON, CardTarget.SELF);
        this.baseMagicNumber = 1;
        this.magicNumber = 1;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(p, p, new FlourishPower(p), 1));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Prosper();
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
