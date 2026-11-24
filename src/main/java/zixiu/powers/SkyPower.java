package zixiu.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import zixiu.ZixiuMod;

/**
 * 天：每回合开始时，对所有敌人施加 1 层曝光。
 */
public class SkyPower extends AbstractPower {

    public static final String POWER_ID = ZixiuMod.makeID("SkyPower");

    public SkyPower(AbstractCreature owner) {
        this.name = "天";
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = 1;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        PowerIcons.apply(this, "Sky");
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = "每回合开始时，对所有敌人施加 1 层曝光。";
    }

    @Override
    public void atStartOfTurn() {
        this.flash();
        for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
            if (!monster.isDeadOrEscaped()) {
                ExposurePower.apply(monster, this.amount);
            }
        }
    }
}
