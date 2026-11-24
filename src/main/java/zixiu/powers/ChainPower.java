package zixiu.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import zixiu.ZixiuMod;

/**
 * 连书：血条下的标记，显示本回合当前的连书数。
 *
 * 真正的状态在 {@link zixiu.core.FusionTracker}，这里只负责显示——连书断了就把自己摘掉，
 * 图标不会留下一个「连书 0」。
 */
public class ChainPower extends AbstractPower {

    public static final String POWER_ID = ZixiuMod.makeID("ChainPower");

    public ChainPower(AbstractCreature owner) {
        this.name = "连书";
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = 1;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        PowerIcons.apply(this, "Chain");
        updateDescription();
    }

    /** 让图标等于当前连书数；连书为 0 时把自己移除。 */
    public static void sync() {
        if (AbstractDungeon.player == null) {
            return;
        }
        int chain = zixiu.core.FusionTracker.chain();
        AbstractPower existing = AbstractDungeon.player.getPower(POWER_ID);
        if (chain <= 0) {
            if (existing != null) {
                AbstractDungeon.actionManager.addToBottom(
                        new RemoveSpecificPowerAction(AbstractDungeon.player, AbstractDungeon.player, POWER_ID));
            }
            return;
        }
        if (existing instanceof ChainPower) {
            existing.amount = chain;
            existing.updateDescription();
        } else {
            ChainPower power = new ChainPower(AbstractDungeon.player);
            power.amount = chain;
            power.updateDescription();
            AbstractDungeon.player.powers.add(power);
        }
    }

    @Override
    public void updateDescription() {
        this.description = "连书 " + this.amount + "：每接一张与上一张共享部件的成品牌，"
                + "对所有敌人造成 " + (2 * this.amount) + " 点伤害。连书可以跨回合续上。";
    }
}
