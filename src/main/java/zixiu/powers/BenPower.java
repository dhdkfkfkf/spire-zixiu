package zixiu.powers;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import zixiu.ZixiuMod;

/**
 * 本：每回合开始时获得 X 点格挡。
 */
public class BenPower extends AbstractPower {

    public static final String POWER_ID = ZixiuMod.makeID("BenPower");

    public BenPower(AbstractCreature owner, int blockPerTurn) {
        this.name = "本";
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = blockPerTurn;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        // The vanilla atlas names this region "platedarmor" (no underscore). With a miss, region128
        // stays null and FlashPowerEffect throws the first time 本 flashes -- a live crash.
        loadRegion("platedarmor");
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = "回合开始时获得 " + this.amount + " 点格挡。";
    }

    @Override
    public void atStartOfTurn() {
        this.flash();
        addToBot(new GainBlockAction(this.owner, this.owner, this.amount));
    }
}
