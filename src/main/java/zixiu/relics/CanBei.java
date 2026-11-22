package zixiu.relics;

import basemod.abstracts.CustomRelic;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import zixiu.ZixiuMod;

/**
 * 残碑：每场战斗的遗物奖励里，额外出现一个你还没有的九字。
 *
 * 模仿圣主「寻符器」的做法：奖励界面生成时往 room.rewards 里插一条 RewardItem。
 * 它本身是字修专属遗物，不进共享池。
 */
public class CanBei extends CustomRelic {

    public static final String ID = "CanBei";

    public CanBei() {
        super(ZixiuMod.makeID(ID), "canbei.png", RelicTier.RARE, LandingSound.FLAT);
    }

    public static AbstractRelic randomMissing() {
        java.util.List<String> missing = InscriptionRelic.missingIds();
        if (missing.isEmpty()) {
            return null;
        }
        String id = missing.get(AbstractDungeon.relicRng.random(missing.size() - 1));
        return InscriptionRelic.make(id);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new CanBei();
    }
}
