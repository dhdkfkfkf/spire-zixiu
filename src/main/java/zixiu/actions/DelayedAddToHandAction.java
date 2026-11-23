package zixiu.actions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

/**
 * 合出的牌等输入安静下来再进手牌。
 *
 * 选字面板/确认键的最后一次点击如果还按着，牌落到手牌位置会被手牌接住，
 * 表现为「刚合出来的牌自己打出去了」。这里等到没有按下的键再插入。
 */
public class DelayedAddToHandAction extends AbstractGameAction {

    private static final int QUIET_FRAMES = 15;
    private static final int MAX_WAIT = 180;

    private final AbstractCard card;
    private int quiet = 0;
    private int waited = 0;

    public DelayedAddToHandAction(AbstractCard card) {
        this.card = card;
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = 0.0F;
    }

    @Override
    public void update() {
        waited++;
        if (InputHelper.justClickedLeft || Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            quiet = 0;
        } else {
            quiet++;
        }
        if (quiet >= QUIET_FRAMES || waited >= MAX_WAIT) {
            ChooseWordAction.putIntoHandNow(card);
            this.isDone = true;
        }
    }
}
