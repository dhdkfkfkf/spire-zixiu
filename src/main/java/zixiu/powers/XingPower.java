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
 * 行：本回合获得的敏捷，到下回合开始时还回去。
 *
 * 施加的当下敏捷就生效；{@code atStartOfTurn} 只在「下一个回合开始」跑一次，
 * 到那时按 amount 扣回敏捷并把自己摘掉（用一个 spent 标记保证只结算一次，
 * 避免之前「减一次之后既不结束也不再减」的老问题）。
 */
public class XingPower extends AbstractPower {

    public static final String POWER_ID = ZixiuMod.makeID("XingPower");

    private boolean spent;

    public XingPower(AbstractCreature owner) {
        this.name = "行";
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = 1;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        PowerIcons.apply(this, "Xing");
        updateDescription();
    }

    /** 立刻 +dexterity 点敏捷，并挂一个「下回合开始收回」的标记。 */
    public static void apply(AbstractPlayer player, int dexterity) {
        AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(player, player, new DexterityPower(player, dexterity), dexterity));
        AbstractDungeon.actionManager.addToBottom(
                new ApplyPowerAction(player, player, new XingPower(player), dexterity));
    }

    @Override
    public void updateDescription() {
        this.description = "本回合获得 " + this.amount + " 点敏捷，下回合开始时失去。";
    }

    @Override
    public void atStartOfTurn() {
        if (this.spent) {
            return;
        }
        this.spent = true;
        this.flash();
        addToBot(new ReducePowerAction(this.owner, this.owner, DexterityPower.POWER_ID, this.amount));
        addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
    }
}
