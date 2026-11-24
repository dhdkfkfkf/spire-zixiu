package zixiu.relics;

import com.megacrit.cardcrawl.relics.AbstractRelic;
import zixiu.ZixiuMod;

/**
 * 连书：接字成链的凭据。
 *
 * 真正的判定在 {@link zixiu.core.FusionTracker#recordWord} 与 {@link zixiu.core.CardPlayHook}，
 * 这里只作为「有没有这个能力」的开关——初始遗物，砚秋默认带着。
 */
public class LianShu extends ZixiuRelic {

    public static final String ID = "LianShu";

    public LianShu() {
        super(ZixiuMod.makeID(ID), "lianshu.png", RelicTier.STARTER, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new LianShu();
    }
}
