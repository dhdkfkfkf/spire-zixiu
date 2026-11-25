package zixiu.powers;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import zixiu.ZixiuMod;

/**
 * 合：每当你合出一个字，抽 1 并获得 1 点格挡。不限次数。
 */
public class HePower extends AbstractPower {

    public static final String POWER_ID = ZixiuMod.makeID("HePower");

    public HePower(AbstractCreature owner) {
        this.name = "合";
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = 1;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        PowerIcons.apply(this, "He");
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = "每当你合出一个字，抽 " + this.amount + " 张牌并获得 " + this.amount + " 点格挡。";
    }

    public void onFusion() {
        this.flash();
        addToBot(new GainBlockAction(this.owner, this.owner, this.amount));
        addToBot(new DrawCardAction(this.owner, this.amount));
    }
}
