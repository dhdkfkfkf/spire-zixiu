package zixiu.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import zixiu.ZixiuMod;

/**
 * 炅：每当你拆解一张牌，对所有敌人施加 1 层曝光。
 */
public class JiongPower extends AbstractPower {

    public static final String POWER_ID = ZixiuMod.makeID("JiongPower");

    public JiongPower(AbstractCreature owner) {
        this.name = "炅";
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = 1;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        loadRegion("focus");
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = "每当你拆解一张牌，对所有敌人施加 1 层曝光。";
    }

    public void onSplit() {
        this.flash();
        for (AbstractMonster monster : com.megacrit.cardcrawl.dungeons.AbstractDungeon.getMonsters().monsters) {
            if (!monster.isDeadOrEscaped()) {
                ExposurePower.apply(monster, this.amount);
            }
        }
    }
}
