package zixiu.powers;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import zixiu.ZixiuMod;

/**
 * 合：每当你合出一个字，抽 1 并获得 1 点格挡（每回合最多 amount 次）。
 */
public class HePower extends AbstractPower {

    public static final String POWER_ID = ZixiuMod.makeID("HePower");

    private int fusionsThisTurn = 0;

    public HePower(AbstractCreature owner, int perTurn) {
        this.name = "合";
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = perTurn;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        loadRegion("flex");
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = "每当你合出一个字，抽 1 张牌并获得 1 点格挡（每回合最多 "
                + this.amount + " 次）。";
    }

    @Override
    public void atStartOfTurn() {
        this.fusionsThisTurn = 0;
    }

    public void onFusion() {
        if (this.fusionsThisTurn >= this.amount) {
            return;
        }
        this.fusionsThisTurn++;
        this.flash();
        addToBot(new GainBlockAction(this.owner, this.owner, 1));
        addToBot(new DrawCardAction(this.owner, 1));
    }
}
