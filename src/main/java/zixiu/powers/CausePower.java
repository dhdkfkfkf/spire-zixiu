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
 * 因：每当你打出一张成品牌，对所有敌人造成等同于本回合合字次数的伤害。
 *
 * 直接抽上手的成品牌在本回合没有合过字，伤害为 0——它是为「合出来的字」付钱的。
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
        loadRegion("dexterity");
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = "每当你打出一张成品牌，对所有敌人造成等同于本回合合字次数的伤害。";
    }

    public void onWordPlayed() {
        int damage = FusionTracker.fusionsThisTurn();
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
