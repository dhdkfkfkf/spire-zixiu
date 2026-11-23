package zixiu.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDiscardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;
import zixiu.cards.CardComponents;
import zixiu.core.FusionTracker;
import zixiu.lexicon.WordSpec;

import java.util.ArrayList;
import java.util.List;

/**
 * 选字面板：一组部件对应多个字时，让玩家挑一个成型。
 * 面板复用 CardRewardScreen.customCombatOpen。
 */
public class ChooseWordAction extends AbstractGameAction {

    private final List<WordSpec> options;
    private boolean opened = false;

    public ChooseWordAction(List<WordSpec> options) {
        this.options = options;
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_FAST;
    }

    @Override
    public void update() {
        if (!opened) {
            ArrayList<AbstractCard> cards = new ArrayList<AbstractCard>();
            for (WordSpec spec : options) {
                AbstractCard card = CardComponents.create(spec.glyph());
                if (card != null) {
                    cards.add(card);
                }
            }
            AbstractDungeon.cardRewardScreen.customCombatOpen(cards, "选择要写的字", false);
            this.opened = true;
            return;
        }

        if (AbstractDungeon.cardRewardScreen.discoveryCard != null) {
            AbstractCard picked = AbstractDungeon.cardRewardScreen.discoveryCard;
            AbstractDungeon.cardRewardScreen.discoveryCard = null;
            putIntoHand(picked);
            this.isDone = true;
        }
    }

    public static void putIntoHand(AbstractCard card) {
        zixiu.ui.DeferredCardInsert.enqueue(card);
    }

    public static void putIntoHandNow(AbstractCard card) {
        AbstractCard copy = card.makeStatEquivalentCopy();
        copy.current_x = -1000.0F * Settings.xScale;
        FusionTracker.markFresh(copy);
        if (AbstractDungeon.player.hand.size() < 10) {
            AbstractDungeon.effectList.add(new ShowCardAndAddToHandEffect(
                    copy, (float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
        } else {
            AbstractDungeon.effectList.add(new ShowCardAndAddToDiscardEffect(
                    copy, (float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
        }
    }
}
