package zixiu.relics;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import zixiu.ZixiuMod;

/**
 * 残砚：每场战斗第一次合字后，抽 1 张牌。
 */
public class ZanYan extends ZixiuRelic {

    public static final String ID = "ZanYan";

    private boolean usedThisCombat = false;

    public ZanYan() {
        super(ZixiuMod.makeID(ID), "zanyan.png", RelicTier.COMMON, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void atBattleStart() {
        this.usedThisCombat = false;
    }

    public void onFusion() {
        if (this.usedThisCombat) {
            return;
        }
        this.usedThisCombat = true;
        this.flash();
        AbstractDungeon.actionManager.addToBottom(
                new DrawCardAction(AbstractDungeon.player, 1));
    }

    @Override
    public AbstractRelic makeCopy() {
        return new ZanYan();
    }
}
