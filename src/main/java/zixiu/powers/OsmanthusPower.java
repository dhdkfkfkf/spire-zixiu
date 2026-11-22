package zixiu.powers;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import zixiu.ZixiuMod;

/**
 * 桂：每当你打出一张技能牌，获得 2 点格挡。
 */
public class OsmanthusPower extends AbstractPower {

    public static final String POWER_ID = ZixiuMod.makeID("OsmanthusPower");

    public OsmanthusPower(AbstractCreature owner) {
        this.name = "桂";
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
        this.description = "每当你打出一张技能牌，获得 2 点格挡。";
    }

    public void onSkillPlayed() {
        this.flash();
        addToBot(new GainBlockAction(this.owner, this.owner, 2));
    }
}
