package zixiu.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import zixiu.powers.GuardPower;

/**
 * 保：护身。获得格挡，并让本回合受到的攻击伤害减半（向下取整）。
 *
 * 未升级：只挡下一次攻击；升级：获得更多格挡，且本回合所有攻击都减半。
 */
public class Guard extends ZixiuCard {

    public static final String ID = "Guard";
    public static final String GLYPH = "保";

    public Guard() {
        super(ID, 1, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.SELF);
        this.baseBlock = 8;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(
                new com.megacrit.cardcrawl.actions.common.GainBlockAction(p, p, this.block));
        // 升级后是「本回合所有攻击减半」，未升级只挡下一次
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p,
                new GuardPower(p, this.upgraded), 1));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Guard();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBlock(4);
            // 升级后挡「所有」攻击，描述要换
            String upgradeDesc = strings(ID).UPGRADE_DESCRIPTION;
            if (upgradeDesc != null) {
                this.rawDescription = upgradeDesc;
                initializeDescription();
            }
        }
    }
}
