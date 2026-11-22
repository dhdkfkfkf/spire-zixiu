package zixiu.relics;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import zixiu.cards.ComponentCard;

/** 前：每回合第偶数次打出部件牌时，抽 1 张牌。 */
public class Qian extends InscriptionRelic {

    public static final String ID = "Qian";

    private int playedThisTurn = 0;

    public Qian() {
        super(ID, "qian.png");
    }

    @Override
    public void atTurnStart() {
        playedThisTurn = 0;
    }

    @Override
    public void onCardPlayed(AbstractCard card) {
        if (!(card instanceof ComponentCard)) {
            return;
        }
        playedThisTurn++;
        if (playedThisTurn % 2 != 0) {
            return;
        }
        this.flash();
        addToBot(new DrawCardAction(AbstractDungeon.player, 1));
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new Qian();
    }
}
