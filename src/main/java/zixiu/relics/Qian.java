package zixiu.relics;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import zixiu.cards.ComponentCard;

/** 前：每回合打出一张部件牌和一张成品牌后，抽 1 张牌（一回合一次）。 */
public class Qian extends InscriptionRelic {

    public static final String ID = "Qian";

    /** 本回合打出过部件牌 / 成品牌没有。 */
    private boolean playedComponent;
    private boolean playedWord;
    /** 本回合是否已经抽过（一对只抽一次）。 */
    private boolean drawnThisTurn;

    public Qian() {
        super(ID, "qian.png");
    }

    @Override
    public void atTurnStart() {
        playedComponent = false;
        playedWord = false;
        drawnThisTurn = false;
    }

    @Override
    public void onCardPlayed(AbstractCard card) {
        boolean component = card instanceof ComponentCard;
        boolean word = !component && zixiu.cards.CardComponents.glyphOf(card) != null;
        if (!component && !word) {
            return;
        }
        if (component) {
            playedComponent = true;
        } else {
            playedWord = true;
        }
        // 一部件 + 一成品牌，两样都打过了才抽，一回合一次
        if (drawnThisTurn || !playedComponent || !playedWord) {
            return;
        }
        drawnThisTurn = true;
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
