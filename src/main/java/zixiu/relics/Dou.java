package zixiu.relics;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import zixiu.core.FusionTracker;

/** 斗：每回合第奇数次合出字时，抽 1 张牌。 */
public class Dou extends InscriptionRelic {

    public static final String ID = "Dou";
    public static final int DRAW = 1;

    public Dou() {
        super(ID, "dou.png");
    }

    @Override
    public void onFusion() {
        // 计数在本回合的合字里已经包含当前这一次。
        if (FusionTracker.fusionsThisTurn() % 2 == 0) {
            return;
        }
        this.flash();
        addToBot(new DrawCardAction(AbstractDungeon.player, DRAW));
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new Dou();
    }
}
