package zixiu.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import zixiu.ZixiuMod;

/**
 * 灼烧：回合结束时失去 X 点生命，不递减。
 */
public class BurnPower extends AbstractPower {

    public static final String POWER_ID = ZixiuMod.makeID("BurnPower");

    public BurnPower(AbstractCreature owner, int amount) {
        this.name = "灼烧";
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.DEBUFF;
        this.isTurnBased = false;
        PowerIcons.apply(this, "Burn");
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = "回合结束时失去 " + this.amount + " 点生命，然后层数减 1。";
    }

    @Override
    public void atEndOfRound() {
        if (this.amount <= 0) {
            return;
        }
        this.flash();
        addToBot(new DamageAction(this.owner,
                new DamageInfo(this.owner, this.amount, DamageInfo.DamageType.HP_LOSS),
                AbstractGameAction.AttackEffect.FIRE));
        // 回合结束烧掉一层（伤害按扣减前的层数结算）
        this.amount--;
        if (this.amount <= 0) {
            AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(
                    this.owner, this.owner, POWER_ID));
        } else {
            updateDescription();
        }
    }

    /**
     * 统一的灼烧入口：处理「杰」的额外层数与「王」的格挡触发。
     */
    public static void apply(AbstractCreature target, int baseAmount) {
        if (target == null || baseAmount <= 0) {
            return;
        }
        AbstractPlayer player = AbstractDungeon.player;
        int amount = baseAmount;
        if (player != null) {
            AbstractPower jie = player.getPower(JiePower.POWER_ID);
            if (jie instanceof JiePower) {
                amount += ((JiePower) jie).consumeBonus();
            }
            amount += zixiu.core.RelicHooks.burnBonus(player);
        }
        AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(target, player, new BurnPower(target, amount), amount));
        if (player != null) {
            AbstractPower wang = player.getPower(WangPower.POWER_ID);
            if (wang instanceof WangPower) {
                ((WangPower) wang).onBurnApplied();
            }
        }
    }
}
