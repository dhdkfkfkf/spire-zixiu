package zixiu.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import zixiu.ZixiuMod;

/**
 * 灶：这一回合里，你每拆一张牌，就往灶里添一把柴——所有敌人各加「amount」层灼烧。
 *
 * 只在打出它的那一回合有效，回合结束（敌人打完）自己摘掉。
 * 走 {@link BurnPower#apply}，所以「杰」「王」这些施加灼烧的钩子照常生效。
 */
public class ZaoPower extends AbstractPower {

    public static final String POWER_ID = ZixiuMod.makeID("ZaoPower");

    public ZaoPower(AbstractCreature owner, int layers) {
        this.name = "灶";
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = Math.max(1, layers);
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        PowerIcons.apply(this, "Zao");
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = "本回合每当你拆解一张牌，对所有敌人施加 " + this.amount + " 层灼烧。";
    }

    public void onSplit() {
        if (AbstractDungeon.getMonsters() == null) {
            return;
        }
        this.flash();
        for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
            if (monster == null || monster.isDeadOrEscaped()) {
                continue;
            }
            BurnPower.apply(monster, this.amount);
        }
    }

    @Override
    public void atEndOfRound() {
        addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
    }
}
