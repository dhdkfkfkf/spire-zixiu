package zixiu.powers;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import zixiu.ZixiuMod;

/**
 * 王：每当你施加灼烧，获得 1 点格挡。
 */
public class WangPower extends AbstractPower {

    public static final String POWER_ID = ZixiuMod.makeID("WangPower");

    public WangPower(AbstractCreature owner) {
        this.name = "王";
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = 1;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        PowerIcons.apply(this, "Wang");
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = "每当你施加灼烧，获得 1 点格挡。";
    }

    public void onBurnApplied() {
        this.flash();
        addToBot(new GainBlockAction(this.owner, this.owner, this.amount));
    }
}
