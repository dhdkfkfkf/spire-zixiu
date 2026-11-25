package zixiu.core;

import basemod.interfaces.OnPlayerTurnStartSubscriber;
import basemod.interfaces.OnStartBattleSubscriber;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import zixiu.character.ZixiuCharacter;
import zixiu.powers.YanlingPower;

public class TurnReset implements OnPlayerTurnStartSubscriber, OnStartBattleSubscriber {

    @Override
    public void receiveOnBattleStart(com.megacrit.cardcrawl.rooms.AbstractRoom room) {
        // 「因」按本次战斗的合字次数算，开战时清零
        FusionTracker.resetCombat();
        // 字修的手牌上限 20（BaseMod 把原版写死的 10 换成了这个字段）；
        // 拆/合产出的牌走 hand.addToHand 直接插入，不占上限，所以可以超过 20 张。
        basemod.BaseMod.MAX_HAND_SIZE = AbstractDungeon.player instanceof ZixiuCharacter
                ? 20 : basemod.BaseMod.DEFAULT_MAX_HAND_SIZE;
    }

    @Override
    public void receiveOnPlayerTurnStart() {
        FusionTracker.clear();
        // 九字真言·言灵：回合开始触发一次当前言灵（没集齐时这是唯一一次）
        YanlingPower.onPlayerTurnStart(AbstractDungeon.player);
    }
}
