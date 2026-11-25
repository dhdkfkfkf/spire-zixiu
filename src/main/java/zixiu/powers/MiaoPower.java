package zixiu.powers;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import zixiu.ZixiuMod;

/**
 * 淼：每次连书接上，获得等同于连书层数的格挡。
 *
 * amount = 倍率（默认 1，两张淼就是 ×2）。判定点在 {@link zixiu.core.CardPlayHook}。
 */
public class MiaoPower extends AbstractPower {

    public static final String POWER_ID = ZixiuMod.makeID("MiaoPower");

    public MiaoPower(AbstractCreature owner) {
        this.name = "淼";
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = 1;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        PowerIcons.apply(this, "Miao");
        updateDescription();
    }

    @Override
    public void updateDescription() {
        if (this.amount <= 1) {
            this.description = "每当你触发连书，获得等同于连书层数的格挡。";
        } else {
            this.description = "每当你触发连书，获得 连书层数 × " + this.amount + " 的格挡。";
        }
    }

    /** 连书接上时由 CardPlayHook 调用（chain 是本次之后的层数，> 0）。 */
    public static void onChain(AbstractCreature player, int chain) {
        if (player == null || chain <= 0) {
            return;
        }
        AbstractPower power = player.getPower(POWER_ID);
        if (!(power instanceof MiaoPower)) {
            return;
        }
        power.flash();
        AbstractDungeon.actionManager.addToBottom(
                new GainBlockAction(player, player, chain * power.amount));
    }
}
