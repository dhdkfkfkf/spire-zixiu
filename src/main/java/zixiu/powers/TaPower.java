package zixiu.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import zixiu.ZixiuMod;

/**
 * 沓：接下来 N 张攻击牌，从曝光得到的伤害加成翻倍。
 *
 * 曝光是「受到伤害 +X」的固定加法（见 ExposurePower.atDamageReceive）。这里开一个窗口，
 * 窗口内按 2X 计算。窗口由 {@link zixiu.core.CardPlayHook} 维护：
 * 打出任意一张牌就关掉上一张的窗口，打出攻击牌时消耗一层并打开窗口——
 * 所以窗口正好覆盖那张攻击牌的整段结算（多段攻击也算）。
 */
public class TaPower extends AbstractPower {

    public static final String POWER_ID = ZixiuMod.makeID("TaPower");

    private static boolean windowOpen;

    public TaPower(AbstractCreature owner, int amount) {
        this.name = "沓";
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        // 暂时借用曝光图标（效果就是翻倍曝光加成）；有专门图标再换
        PowerIcons.apply(this, "Exposure");
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = "接下来 " + this.amount + " 张攻击牌从曝光得到的伤害加成翻倍。";
    }

    /** 打出攻击牌时调用：有层数就消耗一层并打开翻倍窗口。 */
    public static void onAttackCardPlayed(AbstractCreature player) {
        if (player == null) {
            return;
        }
        AbstractPower power = player.getPower(POWER_ID);
        if (!(power instanceof TaPower) || power.amount <= 0) {
            return;
        }
        power.amount--;
        power.flash();
        power.updateDescription();
        windowOpen = true;
        if (power.amount <= 0) {
            // 层数用完：图标撤掉，但窗口要等这张攻击牌结算完才关
            AbstractDungeon.actionManager.addToTop(
                    new RemoveSpecificPowerAction(player, player, power));
        }
    }

    /** 换牌/回合结束时关窗，避免翻倍效果漏到别的伤害上。 */
    public static void closeWindow() {
        windowOpen = false;
    }

    /** 求解器镜像用：窗口是否开着。 */
    public static boolean windowOpen() {
        return windowOpen;
    }

    /** 求解器镜像用：写回窗口状态。 */
    public static void setWindowOpen(boolean open) {
        windowOpen = open;
    }

    /** 窗口开着、并且挨打的是敌人时才翻倍（玩家自己吃曝光不翻）。 */
    public static boolean isWindowOpenFor(AbstractCreature target) {
        return windowOpen && target instanceof AbstractMonster;
    }

    @Override
    public void atEndOfRound() {
        windowOpen = false;
    }
}
