package zixiu.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import zixiu.ZixiuMod;

/**
 * 曝光：受到的伤害 +X，每受到一次伤害后减 1。一次性爆发资源。
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
        loadRegion("vulnerable");
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = "受到的伤害 +" + this.amount + "，每次受伤后减 1。";
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type) {
        if (this.amount <= 0) {
            return damage;
        }
        return damage + this.amount;
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (this.amount > 0) {
            this.amount--;
            if (this.amount <= 0) {
                AbstractDungeon.actionManager.addToTop(
                        new com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction(
                                this.owner, this.owner, POWER_ID));
            } else {
                updateDescription();
            }
        }
        return damageAmount;
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
