package zixiu.powers;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import zixiu.ZixiuMod;

/**
 * 昌：每回合首次施加曝光时，抽 X 张牌。
 */
public class FlourishPower extends AbstractPower {

    public static final String POWER_ID = ZixiuMod.makeID("FlourishPower");

    private boolean usedThisTurn = false;

    public FlourishPower(AbstractCreature owner, int cards) {
        this.name = "昌";
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = cards;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        PowerIcons.apply(this, "Flourish");
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = "每回合首次施加曝光时，抽 " + this.amount + " 张牌。";
    }

    public void onExposureApplied() {
        if (this.usedThisTurn) {
            return;
        }
        this.usedThisTurn = true;
        this.flash();
        addToBot(new DrawCardAction(this.owner, this.amount));
    }

    @Override
    public void atStartOfTurn() {
        this.usedThisTurn = false;
    }
}
