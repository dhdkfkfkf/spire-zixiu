package zixiu.powers;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import zixiu.ZixiuMod;

/**
 * 因：每当你打出一张成品牌，获得 1 点格挡。
 */
public class CausePower extends AbstractPower {

    public static final String POWER_ID = ZixiuMod.makeID("CausePower");

    public CausePower(AbstractCreature owner) {
        this.name = "因";
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = 1;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        loadRegion("dexterity");
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = "每当你打出一张成品牌，获得 1 点格挡。";
    }

    public void onWordPlayed() {
        this.flash();
        addToBot(new GainBlockAction(this.owner, this.owner, this.amount));
    }
}
