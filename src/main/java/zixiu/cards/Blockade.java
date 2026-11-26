package zixiu.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import zixiu.powers.DuPower;

/**
 * 杜：堵不如疏——打进肉里的每一刀，都记成账。
 *
 * 本回合内，你每有一次攻击造成未被格挡的伤害，获得 1 金币。
 */
public class Blockade extends ZixiuCard {

    public static final String ID = "Blockade";
    public static final String GLYPH = "杜";

    public Blockade() {
        super(ID, 2, CardType.SKILL, CardRarity.COMMON, CardTarget.SELF);
        this.baseMagicNumber = 1;
        this.magicNumber = 1;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(p, p, new DuPower(p), 1));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Blockade();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            // 升级：2 费 -> 1 费
            upgradeBaseCost(1);
        }
    }
}
