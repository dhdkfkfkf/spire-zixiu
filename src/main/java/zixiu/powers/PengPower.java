package zixiu.powers;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import zixiu.ZixiuMod;

/**
 * 朋：每当你拆解一张牌，抽 1 张牌。不限次数（原来每回合最多 X 次）。
 */
public class PengPower extends AbstractPower {

    public static final String POWER_ID = ZixiuMod.makeID("PengPower");

    public PengPower(AbstractCreature owner) {
        this.name = "朋";
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = 1;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        PowerIcons.apply(this, "Peng");
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = "每当你拆解一张牌，抽 1 张牌。";
    }

    /** 拆解一张牌就抽一张，没有次数上限。 */
    public void onSplit() {
        this.flash();
        addToBot(new DrawCardAction(this.owner, 1));
    }
}
