package zixiu.relics;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

/** 临：战斗开始时抽一张牌。 */
public class Lin extends InscriptionRelic {

    public static final String ID = "Lin";

    public Lin() {
        super(ID, "lin.png");
    }

    @Override
    public void atBattleStart() {
        this.flash();
        addToBot(new DrawCardAction(AbstractDungeon.player, 1));
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new Lin();
    }
}
