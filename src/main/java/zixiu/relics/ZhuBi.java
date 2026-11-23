package zixiu.relics;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import zixiu.ZixiuMod;

/**
 * 朱笔：每当你合出一个成品牌，抽 1 张牌（每回合上限 1）。
 */
public class ZhuBi extends ZixiuRelic {

    public static final String ID = "ZhuBi";

    private boolean usedThisTurn = false;

    public ZhuBi() {
        super(ZixiuMod.makeID(ID), "zhubi.png", RelicTier.UNCOMMON, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void atTurnStart() {
        this.usedThisTurn = false;
    }

    public void onFusion() {
        if (this.usedThisTurn) {
            return;
        }
        this.usedThisTurn = true;
        this.flash();
        AbstractDungeon.actionManager.addToBottom(
                new DrawCardAction(AbstractDungeon.player, 1));
    }

    @Override
    public AbstractRelic makeCopy() {
        return new ZhuBi();
    }
}
