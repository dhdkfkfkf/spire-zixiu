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
    /** 每张技能牌的格挡，叠一份就 +2。 */
    public static final int BLOCK = 3;

    public OsmanthusPower(AbstractCreature owner) {
        this.name = "桂";
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = BLOCK;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        PowerIcons.apply(this, "Osmanthus");
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = "每当你打出一张技能牌，获得 " + this.amount + " 点格挡。";
    }

    public void onSkillPlayed() {
        this.flash();
        addToBot(new GainBlockAction(this.owner, this.owner, this.amount));
    }
}
