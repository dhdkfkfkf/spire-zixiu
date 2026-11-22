package zixiu.relics;

import basemod.abstracts.CustomRelic;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import zixiu.ZixiuMod;

/**
 * 火折子：每回合第一次施加灼烧时，额外施加 1 层。
 */
public class HuoZheZi extends CustomRelic {

    public static final String ID = "HuoZheZi";

    private boolean usedThisTurn = false;

    public HuoZheZi() {
        super(ZixiuMod.makeID(ID), "huozhezi.png", RelicTier.COMMON, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void atTurnStart() {
        this.usedThisTurn = false;
    }

    /** 返回本回合尚未用掉的额外层数。 */
    public int consumeBonus() {
        if (this.usedThisTurn) {
            return 0;
        }
        this.usedThisTurn = true;
        this.flash();
        return 1;
    }

    @Override
    public AbstractRelic makeCopy() {
        return new HuoZheZi();
    }
}
