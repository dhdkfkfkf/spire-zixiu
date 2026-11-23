package zixiu.ui;

import basemod.interfaces.PostUpdateSubscriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import zixiu.actions.ChooseWordAction;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 合出的牌延迟进场：等输入安静下来再插进手牌。
 *
 * 之前用的是 Action，回合结束/切屏时 actionManager.clear() 会把待插入的牌一起清掉，
 * 材料已经烧掉、结果却没出现——看起来就是「合出来的牌自己消失了」。
 * 改成常驻订阅者，待插入列表不会被清空，只要还活着就一定会落地。
 */
public final class DeferredCardInsert implements PostUpdateSubscriber {

    private static final int QUIET_FRAMES = 12;
    private static final List<AbstractCard> PENDING = new ArrayList<AbstractCard>();
    private static int quiet = 0;

    public static void enqueue(AbstractCard card) {
        if (card != null) {
            PENDING.add(card);
        }
    }

    public static int pending() {
        return PENDING.size();
    }

    /** 合字落地记录：万一牌又消失，能看出它到底有没有进过手牌。 */
    private static void log(String line) {
        try {
            FileWriter writer = new FileWriter("zixiu-fusion.log", true);
            writer.write(System.currentTimeMillis() + " " + line + System.lineSeparator());
            writer.close();
        } catch (IOException ignored) {
            // 记不上日志不影响游戏。
        }
    }

    @Override
    public void receivePostUpdate() {
        if (PENDING.isEmpty() || AbstractDungeon.player == null) {
            return;
        }
        if (InputHelper.justClickedLeft || Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            quiet = 0;
            return;
        }
        quiet++;
        if (quiet < QUIET_FRAMES) {
            return;
        }
        // 还在别的界面里就先等着，但绝不丢弃——否则材料白烧。
        if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.NONE) {
            return;
        }
        for (AbstractCard card : new ArrayList<AbstractCard>(PENDING)) {
            ChooseWordAction.putIntoHandNow(card);
            log("insert " + card.cardID + " hand=" + AbstractDungeon.player.hand.size());
        }
        PENDING.clear();
        quiet = 0;
    }
}
