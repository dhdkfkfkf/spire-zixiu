package zixiu.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDiscardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;
import zixiu.cards.Huo;
import zixiu.cards.Kou;
import zixiu.cards.Mu;

import java.util.ArrayList;

/**
 * 战斗开始时，从 3 张随机部件牌里选 1 张加入手牌。
 * 结构照抄原版的 ChooseOneColorless：先 customCombatOpen，再轮询 discoveryCard。
 */
public class ChooseOneComponentAction extends AbstractGameAction {

    private boolean retrieveCard = false;

    public ChooseOneComponentAction() {
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_FAST;
        this.amount = 1;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            AbstractDungeon.cardRewardScreen.customCombatOpen(generateChoices(), CardRewardScreen.TEXT[1], false);
            this.tickDuration();
            return;
        }

        if (!this.retrieveCard && AbstractDungeon.cardRewardScreen.discoveryCard != null) {
            AbstractCard card = AbstractDungeon.cardRewardScreen.discoveryCard.makeStatEquivalentCopy();
            card.current_x = -1000.0F * Settings.xScale;

            if (AbstractDungeon.player.hand.size() < 10) {
                AbstractDungeon.effectList.add(new ShowCardAndAddToHandEffect(
                        card, (float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
            } else {
                AbstractDungeon.effectList.add(new ShowCardAndAddToDiscardEffect(
                        card, (float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
            }

            this.retrieveCard = true;
            AbstractDungeon.cardRewardScreen.discoveryCard = null;
        }

        this.tickDuration();
    }

    /** 从 10 个部件里随机抽 3 张（以前固定给 火/木/口）。 */
    private ArrayList<AbstractCard> generateChoices() {
        ArrayList<zixiu.lexicon.Component> pool =
                new ArrayList<zixiu.lexicon.Component>(java.util.Arrays.asList(
                        zixiu.lexicon.Component.values()));
        ArrayList<AbstractCard> choices = new ArrayList<AbstractCard>();
        for (int i = 0; i < 3 && !pool.isEmpty(); i++) {
            int index = AbstractDungeon.cardRandomRng.random(pool.size() - 1);
            AbstractCard card = zixiu.cards.CardComponents.createComponent(pool.remove(index));
            if (card != null) {
                choices.add(card);
            }
        }
        return choices;
    }
}
