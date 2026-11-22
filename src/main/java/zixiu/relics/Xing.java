package zixiu.relics;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import zixiu.core.FusionTracker;
import zixiu.powers.XingPower;

/** 行：回合结束时，若本回合既合过字又拆过牌，下回合获得 5 点敏捷（下回合结束时移除）。 */
public class Xing extends InscriptionRelic {

    public static final String ID = "Xing";
    public static final int DEXTERITY = 5;

    public Xing() {
        super(ID, "xing.png");
    }

    @Override
    public void onPlayerEndTurn() {
        if (FusionTracker.fusionsThisTurn() <= 0 || FusionTracker.splitsThisTurn() <= 0) {
            return;
        }
        this.flash();
        XingPower.apply(AbstractDungeon.player, DEXTERITY);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new Xing();
    }
}
