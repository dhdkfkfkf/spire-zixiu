package zixiu.cards;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.util.ArrayList;
import java.util.List;

/**
 * 淡：看淡了就不受影响。
 *
 * <p>移除自身所有负面状态；升级后再移除所有敌人身上的正面状态。费用固定 2。
 */
public class Bland extends ZixiuCard {

    public static final String ID = "Bland";
    public static final String GLYPH = "淡";

    public Bland() {
        super(ID, 2, CardType.SKILL, CardRarity.RARE, CardTarget.ALL);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        removePowers(p, AbstractPower.PowerType.DEBUFF);
        if (this.upgraded) {
            for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
                if (!monster.isDeadOrEscaped()) {
                    removePowers(monster, AbstractPower.PowerType.BUFF);
                }
            }
        }
    }

    private void removePowers(com.megacrit.cardcrawl.core.AbstractCreature target,
                              AbstractPower.PowerType type) {
        List<String> ids = new ArrayList<String>();
        for (AbstractPower power : target.powers) {
            if (power.type == type) {
                ids.add(power.ID);
            }
        }
        for (String id : ids) {
            AbstractDungeon.actionManager.addToBottom(
                    new RemoveSpecificPowerAction(target, target, id));
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Bland();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            String upgradeDesc = strings(ID).UPGRADE_DESCRIPTION;
            if (upgradeDesc != null) {
                this.rawDescription = upgradeDesc;
                initializeDescription();
            }
        }
    }
}
