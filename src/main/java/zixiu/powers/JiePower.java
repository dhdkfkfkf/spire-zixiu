package zixiu.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import zixiu.ZixiuMod;

/**
 * 杰：每回合首次施加灼烧时，额外施加 2 层。
 */
public class JiePower extends AbstractPower {

    public static final String POWER_ID = ZixiuMod.makeID("JiePower");
    public static final int BONUS = 2;

    private boolean usedThisTurn = false;

    public JiePower(AbstractCreature owner) {
        this.name = "杰";
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = BONUS;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        PowerIcons.apply(this, "Jie");
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = "每回合首次施加灼烧时，额外施加 " + this.amount + " 层。";
    }

    /** 返回本回合尚未用掉的额外层数，用掉后归零。 */
    public int consumeBonus() {
        if (this.usedThisTurn) {
            return 0;
        }
        this.usedThisTurn = true;
        this.flash();
        return this.amount;
    }

    @Override
    public void atStartOfTurn() {
        this.usedThisTurn = false;
    }
}
