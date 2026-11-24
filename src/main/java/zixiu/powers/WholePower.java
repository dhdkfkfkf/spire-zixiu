package zixiu.powers;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import zixiu.ZixiuMod;
import zixiu.cards.ComponentCard;

/**
 * 全：每回合开始时，若手牌中有 4 张以上部件牌，抽 1 并获得 1 能量。
 */
public class WholePower extends AbstractPower {

    public static final String POWER_ID = ZixiuMod.makeID("WholePower");

    public WholePower(AbstractCreature owner) {
        this.name = "全";
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = 1;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        PowerIcons.apply(this, "Whole");
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = "回合开始时，若手牌有 4 张以上部件牌，抽 1 并获得 1 能量。";
    }

    @Override
    public void atStartOfTurnPostDraw() {
        int components = 0;
        for (AbstractCard card : AbstractDungeon.player.hand.group) {
            if (card instanceof ComponentCard) {
                components++;
            }
        }
        if (components >= 4) {
            this.flash();
            addToBot(new DrawCardAction(this.owner, 1));
            addToBot(new GainEnergyAction(1));
        }
    }
}
