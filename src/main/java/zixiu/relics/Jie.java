package zixiu.relics;

import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

/** 皆：每当你拆解一张牌，治疗 3 点生命。 */
public class Jie extends InscriptionRelic {

    public static final String ID = "Jie";
    public static final int HEAL = 3;

    public Jie() {
        super(ID, "jie.png");
    }

    @Override
    public void onSplit() {
        this.flash();
        addToBot(new HealAction(AbstractDungeon.player, AbstractDungeon.player, HEAL));
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new Jie();
    }
}
