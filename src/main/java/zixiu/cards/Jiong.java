package zixiu.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import zixiu.powers.JiongPower;

/**
 * 炅：拆字本身就是照明。每当你拆解一张牌，对所有敌人施加曝光。
 *
 * 主动的「施加 3 层曝光，若目标有灼烧则抽 2」已经拆到另一张牌（淼）上。
 */
public class Jiong extends ZixiuCard {

    public static final String ID = "Jiong";
    public static final String GLYPH = "炅";

    public Jiong() {
        super(ID, 2, CardType.POWER, CardRarity.RARE, CardTarget.SELF);
        this.baseMagicNumber = 1;
        this.magicNumber = 1;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(p, p, new JiongPower(p, this.magicNumber), this.magicNumber));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Jiong();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            // 升级改成固有（层数不变）
            this.isInnate = true;
            String upgradeDesc = strings(ID).UPGRADE_DESCRIPTION;
            if (upgradeDesc != null) {
                this.rawDescription = upgradeDesc;
                initializeDescription();
            }
        }
    }
}
