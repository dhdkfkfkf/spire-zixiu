package zixiu.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 「未」：从抽牌堆挑一张牌，免费打出若干次，然后把那张牌消耗掉。
 *
 * 选牌用原版的网格界面（和「烧瓶」类遗物同一套 `GridCardSelectScreen.open`），
 * 可以取消：取消就什么都不做（未本身照常消耗）。
 *
 * 打出用的是原版「双发」的实现方式（{@code DuplicationPower.onUseCard}）：
 * 造一个同效果副本丢进 limbo、标 {@code purgeOnUse}，再塞进卡队列免费（autoplay）打出。
 * 这样打出的牌不会落进任何牌堆，也不会重复结算手牌相关的东西。
 */
public class PlayFromDrawPileAction extends AbstractGameAction {

    private static final Logger logger = LogManager.getLogger(PlayFromDrawPileAction.class);

    /** 打给谁：未指定了敌人，抽出来的攻击牌就打它。 */
    private final AbstractMonster target;
    /** 打出几次。 */
    private final int times;
    private boolean opened = false;

    public PlayFromDrawPileAction(AbstractMonster target, int times) {
        this.target = target;
        this.times = Math.max(1, times);
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_FAST;
    }

    @Override
    public void update() {
        if (AbstractDungeon.player == null) {
            this.isDone = true;
            return;
        }
        if (!this.opened) {
            if (AbstractDungeon.player.drawPile.isEmpty()) {
                this.isDone = true;
                return;
            }
            AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.drawPile, 1,
                    "选择一张牌：打出 " + this.times + " 次后消耗",
                    false, false, true, false);
            this.opened = true;
            return;
        }
        if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.GRID) {
            // 界面还开着（可能已选中、正等确定）：等它关掉再动，别在网格背后开始打牌
            return;
        }
        if (AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            // 取消（界面关掉但没有选牌）：这次就不打了
            this.isDone = true;
            return;
        }
        AbstractCard picked = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
        AbstractDungeon.gridSelectScreen.selectedCards.clear();
        logger.info("[zixiu] wei pick " + picked.cardID + " times=" + this.times);
        for (int i = 0; i < this.times; i++) {
            queuePlay(picked.makeSameInstanceOf());
        }
        AbstractDungeon.actionManager.addToBottom(
                new ExhaustSpecificCardAction(picked, AbstractDungeon.player.drawPile));
        this.isDone = true;
    }

    /** 照「双发」的写法：副本进 limbo、purgeOnUse，再进卡队列免费打出。 */
    private void queuePlay(AbstractCard card) {
        AbstractDungeon.player.limbo.addToBottom(card);
        card.current_x = (float) Settings.WIDTH / 2.0F;
        card.current_y = (float) Settings.HEIGHT / 2.0F;
        card.target_x = (float) Settings.WIDTH / 2.0F;
        card.target_y = (float) Settings.HEIGHT / 2.0F;
        card.purgeOnUse = true;
        if (this.target != null) {
            card.calculateCardDamage(this.target);
        }
        AbstractDungeon.actionManager.addCardQueueItem(
                new CardQueueItem(card, this.target, 0, true, true), true);
    }
}
