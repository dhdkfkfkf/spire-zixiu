package zixiu.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import zixiu.ZixiuMod;

/**
 * 杜：这一回合里，每一记「打到肉里」的攻击都换一枚金币。
 *
 * 判定挂在 {@link zixiu.patches.UnblockedHitGold}（AbstractMonster.damage 之后，
 * 看 lastDamageTaken）：只有攻击伤害（NORMAL）且真的掉血才算一次。
 * amount = 每次多少金币；两张杜就是两枚。回合结束摘掉。
 */
public class DuPower extends AbstractPower {

    public static final String POWER_ID = ZixiuMod.makeID("DuPower");

    public DuPower(AbstractCreature owner) {
        this.name = "杜";
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = 1;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        PowerIcons.apply(this, "Du");
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = "本回合内，你每有一次攻击造成未被格挡的伤害，获得 " + this.amount + " 点金币。";
    }

    /** 一次攻击真的打掉血时调用（由伤害补丁触发）。 */
    public static void onUnblockedAttack() {
        if (AbstractDungeon.player == null) {
            return;
        }
        AbstractPower du = AbstractDungeon.player.getPower(POWER_ID);
        if (!(du instanceof DuPower)) {
            return;
        }
        du.flash();
        AbstractDungeon.player.gainGold(du.amount);
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer) {
            addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
        }
    }
}
