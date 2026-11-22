package zixiu.powers;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import zixiu.ZixiuMod;

/**
 * 朋：每当你拆解一张牌，抽 1 张（每回合最多 amount 次）。
 */
public class PengPower extends AbstractPower {

    public static final String POWER_ID = ZixiuMod.makeID("PengPower");

    private int drawsThisTurn = 0;

    public PengPower(AbstractCreature owner, int perTurn) {
        this.name = "朋";
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = perTurn;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        loadRegion("dexterity");
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = "每当你拆解一张牌，抽 1 张牌（每回合最多 " + this.amount + " 次）。";
    }

    @Override
    public void atStartOfTurn() {
        this.drawsThisTurn = 0;
    }

    public void onSplit() {
        if (this.drawsThisTurn >= this.amount) {
            return;
        }
        this.drawsThisTurn++;
        this.flash();
        addToBot(new DrawCardAction(this.owner, 1));
    }
}
