package zixiu.relics;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import zixiu.core.FusionTracker;
import zixiu.powers.XingPower;

/**
 * 行：这一回合里既合过字又拆过牌，就**立刻**获得 3 点敏捷（本回合生效），下回合开始时失去。
 *
 * 两个条件谁后满足就在哪一刻结算，一回合只给一次。
 */
public class Xing extends InscriptionRelic {

    public static final String ID = "Xing";
    public static final int DEXTERITY = 3;

    private boolean grantedThisTurn;

    public Xing() {
        super(ID, "xing.png");
    }

    @Override
    public void atTurnStart() {
        grantedThisTurn = false;
    }

    @Override
    public void onSplit() {
        // 拆已经记进了计数；这里只要有合过字就成立
        if (FusionTracker.fusionsThisTurn() > 0) {
            grant();
        }
    }

    @Override
    public void onFusion() {
        // onFusion 早于「合字计数 +1」，所以这里只看本回合有没有拆过
        if (FusionTracker.splitsThisTurn() > 0) {
            grant();
        }
    }

    private void grant() {
        if (grantedThisTurn) {
            return;
        }
        grantedThisTurn = true;
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
