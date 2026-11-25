package zixiu.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import zixiu.powers.MiaoPower;

/**
 * 淼：水面托着字。连书每接上一环，水面就托起一层格挡。
 *
 * 砚秋自带连书遗物，所以这张牌进场就有收益。
 */
public class Miao extends ZixiuCard {

    public static final String ID = "Miao";
    public static final String GLYPH = "淼";

    public Miao() {
        super(ID, 1, CardType.POWER, CardRarity.UNCOMMON, CardTarget.SELF);
        this.baseMagicNumber = 1;
        this.magicNumber = 1;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(p, p, new MiaoPower(p), 1));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Miao();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            // 升级改成固有
            this.isInnate = true;
            String upgradeDesc = strings(ID).UPGRADE_DESCRIPTION;
            if (upgradeDesc != null) {
                this.rawDescription = upgradeDesc;
                initializeDescription();
            }
        }
    }
}
