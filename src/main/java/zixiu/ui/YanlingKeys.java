package zixiu.ui;

import basemod.interfaces.PostUpdateSubscriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import zixiu.powers.YanlingPower;

/**
 * 集齐九字后，用数字键 1–9 指定并触发对应的言灵。
 *
 * 只在战斗中、没有弹窗时响应，避免和选牌/菜单抢按键。
 */
public class YanlingKeys implements PostUpdateSubscriber {

    private static final int[] KEYS = {
            Input.Keys.NUM_1, Input.Keys.NUM_2, Input.Keys.NUM_3,
            Input.Keys.NUM_4, Input.Keys.NUM_5, Input.Keys.NUM_6,
            Input.Keys.NUM_7, Input.Keys.NUM_8, Input.Keys.NUM_9
    };

    @Override
    public void receivePostUpdate() {
        // 不要自己调 AbstractDungeon.getCurrRoom()：没有地图节点时（主菜单、选角、加载中）
        // 它会直接抛 NPE。HandActions.inCombat() 里带着 try/catch，复用它。
        AbstractPlayer player = AbstractDungeon.player;
        if (player == null || !HandActions.inCombat()) {
            return;
        }
        for (int i = 0; i < KEYS.length; i++) {
            if (Gdx.input.isKeyJustPressed(KEYS[i])) {
                YanlingPower.triggerIndex(player, i + 1);
                return;
            }
        }
    }
}
