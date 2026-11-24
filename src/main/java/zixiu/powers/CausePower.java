package zixiu.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import zixiu.ZixiuMod;
import zixiu.core.FusionTracker;

/**
 * 因：每当你打出一张成品牌，对所有敌人造成等同于**本次战斗**合字次数的伤害。
 *
 * 战斗刚开始、还没合过字时伤害为 0——它是为「合出来的字」付钱的。
 */
public class CausePower extends AbstractPower {

    public static final String POWER_ID = ZixiuMod.makeID("CausePower");

    public CausePower(AbstractCreature owner) {
        this.name = "因";
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = 1;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        PowerIcons.apply(this, "Cause");
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = "每当你打出一张成品牌，对所有敌人造成等同于本次战斗合字次数的伤害。";
    }

    public void onWordPlayed() {
        int damage = FusionTracker.fusionsThisCombat();
        if (damage <= 0) {
            return;
        }
        this.flash();
        for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
            if (monster == null || monster.isDeadOrEscaped()) {
                continue;
            }
            addToBot(new DamageAction(monster,
                    new DamageInfo(this.owner, damage, DamageInfo.DamageType.THORNS),
                    AbstractGameAction.AttackEffect.FIRE));
        }
    }
}
