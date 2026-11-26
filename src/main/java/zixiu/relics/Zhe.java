package zixiu.relics;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

/** 者：每当你合出一个字，获得 3 点格挡。 */
public class Zhe extends InscriptionRelic {

    public static final String ID = "Zhe";
    public static final int BLOCK = 3;

    public Zhe() {
        super(ID, "zhe.png");
    }

    @Override
    public void onFusion() {
        this.flash();
        addToBot(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, BLOCK));
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new Zhe();
    }
}
