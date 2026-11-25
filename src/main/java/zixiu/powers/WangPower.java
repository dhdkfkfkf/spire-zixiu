package zixiu.powers;

import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import zixiu.ZixiuMod;

/**
 * 王：回合结束的时候，让敌人身上的灼烧多爆一次。
 *
 * 玩家能力的 {@code atEndOfRound} 比怪物早跑（MonsterGroup 先结算玩家的能力），
 * 所以这里先按当前层数结算一次、把层数扣 1，随后 {@link BurnPower} 自己的结算再扣 1 ——
 * 合计「多爆一次、多减 1 层」。
 *
 * amount = 多爆几次；两张王就是多爆两次。
 */
public class WangPower extends AbstractPower {

    public static final String POWER_ID = ZixiuMod.makeID("WangPower");

    public WangPower(AbstractCreature owner) {
        this.name = "王";
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = 1;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        PowerIcons.apply(this, "Wang");
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = "回合结束时，敌人身上的灼烧额外爆发 " + this.amount + " 次。";
    }

    @Override
    public void atEndOfRound() {
        if (AbstractDungeon.getMonsters() == null) {
            return;
        }
        for (int i = 0; i < this.amount; i++) {
            boolean burst = false;
            for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
                if (monster == null || monster.isDeadOrEscaped()) {
                    continue;
                }
                AbstractPower burn = monster.getPower(BurnPower.POWER_ID);
                if (burn == null || burn.amount <= 0) {
                    continue;
                }
                burst = true;
                addToBot(new DamageAction(monster,
                        new DamageInfo(this.owner, burn.amount, DamageInfo.DamageType.HP_LOSS),
                        com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect.FIRE));
                // 多烧掉一层（BurnPower 自己的回合结算还会再减一层）
                burn.amount--;
                if (burn.amount <= 0) {
                    addToBot(new RemoveSpecificPowerAction(monster, this.owner, BurnPower.POWER_ID));
                } else {
                    burn.updateDescription();
                }
            }
            if (burst) {
                this.flash();
            }
        }
    }
}
