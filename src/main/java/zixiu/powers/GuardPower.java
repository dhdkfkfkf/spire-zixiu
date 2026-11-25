package zixiu.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import zixiu.ZixiuMod;

/**
 * 保：本回合受到的攻击伤害减半（向下取整）。
 *
 * 未升级只挡**下一次**攻击（挨一下就移除）；升级后本回合**所有**攻击都减半。
 */
public class GuardPower extends AbstractPower {

    public static final String POWER_ID = ZixiuMod.makeID("GuardPower");

    /** true = 本回合所有攻击；false = 只挡下一次。 */
    private final boolean all;

    public GuardPower(AbstractCreature owner, boolean all) {
        this.name = "保";
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = all ? -1 : 1;
        this.all = all;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        // 暂时借仙的旧图标（盾感），有专门图标再换
        PowerIcons.apply(this, "Immortal");
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = all
                ? "本回合受到的所有攻击伤害减半。"
                : "本回合受到的下一次攻击伤害减半。";
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type) {
        if (type != DamageInfo.DamageType.NORMAL) {
            return damage;
        }
        return (float) Math.floor(damage * 0.5F);
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (!this.all && info.type == DamageInfo.DamageType.NORMAL) {
            addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
        }
        return damageAmount;
    }

    @Override
    public void atEndOfRound() {
        addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
    }
}
