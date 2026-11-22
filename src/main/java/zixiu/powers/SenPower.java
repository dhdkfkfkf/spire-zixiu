package zixiu.powers;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import zixiu.ZixiuMod;

/**
 * 森：每当你拆解一张牌，获得 6 点格挡。
 */
public class SenPower extends AbstractPower {

    public static final String POWER_ID = ZixiuMod.makeID("SenPower");
    public static final int BLOCK = 6;

    public SenPower(AbstractCreature owner) {
        this.name = "森";
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = -1;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        loadRegion("strength");
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = "每当你拆解一张牌，获得 " + BLOCK + " 点格挡。";
    }

    public void onSplit() {
        this.flash();
        addToBot(new GainBlockAction(this.owner, this.owner, BLOCK));
    }
}
