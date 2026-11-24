package zixiu.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import zixiu.ZixiuMod;

/**
 * 曝光：受到的伤害 +X，回合结束时减 1。
 *
 * 原先按「每次受伤 -1」，多段攻击会把层数消耗掉，导致 从/二/品/三/林/淋 这些多段牌
 * 和整个照系增伤互相打架。改成按回合衰减后，多段牌也能吃满。
 */
public class ExposurePower extends AbstractPower {

    public static final String POWER_ID = ZixiuMod.makeID("ExposurePower");

    public ExposurePower(AbstractCreature owner, int amount) {
        this.name = "曝光";
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.DEBUFF;
        this.isTurnBased = false;
        PowerIcons.apply(this, "Exposure");
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = "受到的伤害 +" + this.amount + "，回合结束时减 1。";
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type) {
        if (this.amount <= 0) {
            return damage;
        }
        int bonus = this.amount;
        // 沓：窗口内（正在结算被打出的那张攻击牌）曝光加成翻倍
        if (type == DamageInfo.DamageType.NORMAL && TaPower.isWindowOpenFor(this.owner)) {
            bonus *= 2;
        }
        return damage + bonus;
    }

    @Override
    public void atEndOfRound() {
        if (this.amount <= 1) {
            AbstractDungeon.actionManager.addToBottom(
                    new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
            return;
        }
        this.amount--;
        this.flash();
        updateDescription();
    }

    public static void apply(AbstractCreature target, int amount) {
        if (target == null || amount <= 0) {
            return;
        }
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(
                target, AbstractDungeon.player, new ExposurePower(target, amount), amount));
        if (AbstractDungeon.player != null) {
            AbstractPower flourish = AbstractDungeon.player.getPower(FlourishPower.POWER_ID);
            if (flourish instanceof FlourishPower) {
                ((FlourishPower) flourish).onExposureApplied();
            }
        }
    }
}
