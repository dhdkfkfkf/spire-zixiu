package zixiu.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import zixiu.ZixiuMod;

/**
 * 仙：本回合受到的攻击伤害减半，触发一次后移除。
 */
public class ImmortalPower extends AbstractPower {

    public static final String POWER_ID = ZixiuMod.makeID("ImmortalPower");

    public ImmortalPower(AbstractCreature owner) {
        this.name = "仙";
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = 1;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        loadRegion("intangible");
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = "本回合受到的攻击伤害减半，触发一次后消失。";
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL) {
            return damage * 0.5F;
        }
        return damage;
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (info.type == DamageInfo.DamageType.NORMAL) {
            AbstractDungeon.actionManager.addToTop(
                    new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
        }
        return damageAmount;
    }
}
