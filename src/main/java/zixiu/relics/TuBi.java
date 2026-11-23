package zixiu.relics;

import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import zixiu.ZixiuMod;
import zixiu.actions.ChooseOneComponentAction;

public class TuBi extends ZixiuRelic {

    public static final String ID = "TuBi";

    public TuBi() {
        super(ZixiuMod.makeID(ID),
                "tubi.png",
                RelicTier.STARTER, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void atBattleStartPreDraw() {
        addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
        addToBot(new ChooseOneComponentAction());
        this.flash();
    }

    @Override
    public AbstractRelic makeCopy() {
        return new TuBi();
    }
}
