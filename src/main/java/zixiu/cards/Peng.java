package zixiu.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import zixiu.powers.PengPower;

/**
 * 朋：拆字有人搭手，拆得越多，摸得越多。
 */
public class Peng extends ZixiuCard {

    public static final String ID = "Peng";
    public static final String GLYPH = "朋";

    public Peng() {
        super(ID, 1, CardType.POWER, CardRarity.UNCOMMON, CardTarget.SELF);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p,
                new PengPower(p), 1));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Peng();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            // 升级只降费（效果不限次数，没有数值可加）
            upgradeBaseCost(0);
        }
    }
}
