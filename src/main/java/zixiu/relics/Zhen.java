package zixiu.relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.BarricadePower;
import com.megacrit.cardcrawl.relics.AbstractRelic;

/** 阵：格挡不会在回合开始时消失；每回合结束时获得 6 点格挡。 */
public class Zhen extends InscriptionRelic {

    public static final String ID = "Zhen";
    public static final int BLOCK = 6;

    public Zhen() {
        super(ID, "zhen.png");
    }

    @Override
    public void atBattleStart() {
        this.flash();
        addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                new BarricadePower(AbstractDungeon.player), 1));
    }

    @Override
    public void onPlayerEndTurn() {
        this.flash();
        addToBot(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, BLOCK));
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new Zhen();
    }
}
