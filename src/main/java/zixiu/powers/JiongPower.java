package zixiu.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import zixiu.ZixiuMod;

/**
 * 炅：照到的地方只剩火——敌人不再获得曝光，而是获得等量的灼烧。
 *
 * amount = 倍率（默认 1，两张炅就是 ×2）。
 * 判定点是 {@link ExposurePower#apply}：有炅在场时那次曝光会被整个换成灼烧，不落地。
 */
public class JiongPower extends AbstractPower {

    public static final String POWER_ID = ZixiuMod.makeID("JiongPower");

    public JiongPower(AbstractCreature owner, int rate) {
        this.name = "炅";
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = Math.max(1, rate);
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        PowerIcons.apply(this, "Jiong");
        updateDescription();
    }

    @Override
    public void updateDescription() {
        if (this.amount <= 1) {
            this.description = "敌人不再获得曝光，而是获得等量的灼烧。";
        } else {
            this.description = "敌人不再获得曝光，而是获得 " + this.amount + " 倍层数的灼烧。";
        }
    }

    /**
     * 把「这次要加的曝光」换成等量灼烧。
     *
     * @return true = 已经换掉，调用方不要再给曝光
     */
    public static boolean convert(AbstractCreature target, int layers) {
        if (target == null || layers <= 0 || AbstractDungeon.player == null) {
            return false;
        }
        // 只换敌人的；以后若有牌给自己上曝光，不该自燃
        if (!(target instanceof AbstractMonster)) {
            return false;
        }
        AbstractPower jiong = AbstractDungeon.player.getPower(POWER_ID);
        if (!(jiong instanceof JiongPower)) {
            return false;
        }
        jiong.flash();
        BurnPower.apply(target, layers * jiong.amount);
        return true;
    }
}
