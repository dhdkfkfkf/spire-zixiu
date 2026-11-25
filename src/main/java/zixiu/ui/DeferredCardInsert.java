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
 * 拆/合产出的牌延迟落地：等输入安静下来再插进手牌。
 *
 * 之前用的是 Action，回合结束/切屏时 actionManager.clear() 会把待插入的牌一起清掉，
 * 材料已经烧掉、结果却没出现——看起来就是「合出来的牌自己消失了」。
 * 改成常驻订阅者，待插入列表不会被清空，只要还活着就一定会落地。
 *
 * 还要等按键松开：牌落地时生成在屏幕中央，而拆/合是在选牌界面的「确定」那一下触发的，
 * 手机上手指还按着、位置正好落在新牌上，触摸输入会顺手把最上面那张打出去
 * （手机日志里就是拆完 18ms 后一条 publish on card use，牌直接进弃牌堆）。
 */
public final class DeferredCardInsert implements PostUpdateSubscriber {

    private static final int QUIET_FRAMES = 12;

    /** 谁产出的：合字结果要标「墨迹未干」，拆出来的部件不要。 */
    public enum Kind { FUSION, SPLIT }

    private static final class Pending {
        final AbstractCard card;
        final Kind kind;

        Pending(AbstractCard card, Kind kind) {
            this.card = card;
            this.kind = kind;
        }
    }

    private static final List<Pending> PENDING = new ArrayList<Pending>();
    private static int quiet = 0;

    public static void enqueue(AbstractCard card) {
        enqueue(card, Kind.FUSION);
    }

    public static void enqueue(AbstractCard card, Kind kind) {
        // 不设上限：合字/拆字结果必须全部落地，否则材料已烧、字却没了。
        // 求解器侧的重复调用由它自己的沙盒隔离处理。
        if (card != null) {
            PENDING.add(new Pending(card, kind));
        }
    }

    /** 清空待插入队列（求解器在沙盒前后调用，避免跨世界串状态）。 */
    public static void reset() {
        PENDING.clear();
        quiet = 0;
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
        for (Pending item : new ArrayList<Pending>(PENDING)) {
            if (item.kind == Kind.SPLIT) {
                HandActions.insertSplitResult(item.card);
            } else {
                ChooseWordAction.putIntoHandNow(item.card);
            }
            log(item.kind + " insert " + item.card.cardID
                    + " hand=" + AbstractDungeon.player.hand.size());
        }
        PENDING.clear();
        quiet = 0;
    }
}
