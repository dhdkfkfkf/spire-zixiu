package zixiu.relics;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

/** 兵：每当你打出一张攻击牌，获得 2 点格挡。 */
public class Bing extends InscriptionRelic {

    public static final String ID = "Bing";
    public static final int BLOCK = 2;

    public Bing() {
        super(ID, "bing.png");
    }

    @Override
    public void onCardPlayed(AbstractCard card) {
        if (card == null || card.type != AbstractCard.CardType.ATTACK) {
            return;
        }
        this.flash();
        addToBot(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, BLOCK));
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new Bing();
    }
}
