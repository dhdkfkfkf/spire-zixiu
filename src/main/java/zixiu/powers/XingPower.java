package zixiu.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import zixiu.ZixiuMod;

/**
 * 行：下回合获得 X 点敏捷，下回合结束时移除。
 *
 * 用「见过几个回合开始」来计时，不依赖回合结束各钩子的先后顺序：
 * 在回合 N 结束时施加，回合 N+1 开始时计数到 1，回合 N+1 结束时移除。
 */
public class XingPower extends AbstractPower {

    public static final String POWER_ID = ZixiuMod.makeID("XingPower");

    private int turnsSeen = 0;

    public XingPower(AbstractCreature owner) {
        this.name = "行";
        this.ID = POWER_ID;
        this.owner = owner;
        // amount = 这次给的敏捷；叠一份就 +敏捷（两条「行」就是 +10）
        this.amount = 1;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        PowerIcons.apply(this, "Xing");
        updateDescription();
    }

    public static void apply(AbstractPlayer player, int dexterity) {
        AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(player, player, new DexterityPower(player, dexterity), dexterity));
        AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(player, player, new XingPower(player), dexterity));
    }

    @Override
    public void updateDescription() {
        this.description = "下回合获得 " + this.amount + " 点敏捷，下回合结束时失去。";
    }

    @Override
    public void atStartOfTurn() {
        this.turnsSeen++;
    }

    @Override
    public void atEndOfRound() {
        if (this.turnsSeen < 1) {
            return;
        }
        this.flash();
        addToBot(new ReducePowerAction(this.owner, this.owner, DexterityPower.POWER_ID, this.amount));
        addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
    }
}
