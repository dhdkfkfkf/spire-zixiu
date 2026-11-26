package zixiu.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.random.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zixiu.character.ZixiuEnums;
import zixiu.events.JiuZiBei;
import zixiu.relics.InscriptionRelic;

/**
 * 事件房劫持（照圣主的 `EventInterceptorPatch` 的做法）。
 *
 * 只把事件「登记进池子」是抽不到的：池子里有原版 + 所有 mod 的事件，我们只占几个。
 * 圣主在 `AbstractDungeon.getEvent` 上直接返回自己的事件，所以它的剧情事件必现。
 *
 * 这里给砚秋做同样的保底：每一幕的**第一个事件房**，只要九字还没集齐、九字碑还有次数，
 * 就直接换成九字碑。每幕最多劫持一次，第二幕、第三幕各还有机会。
 */
@SpirePatch(clz = AbstractDungeon.class, method = "getEvent",
        paramtypes = {"com.megacrit.cardcrawl.random.Random"})
public class ForceZixiuEvent {

    private static final Logger logger = LogManager.getLogger(ForceZixiuEvent.class);

    /** 这一幕已经劫持过没有（1~3 幕各一次）。 */
    private static int hijackedAct = -1;
    /** 这一幕已经进过几个事件房（用来让劫持别总砸在第一个房间上）。 */
    private static int roomsThisAct;
    private static int roomsAct = -1;

    /** 新开一局时清空。 */
    public static void resetRun() {
        hijackedAct = -1;
        roomsThisAct = 0;
        roomsAct = -1;
    }

    @SpirePrefixPatch
    public static SpireReturn<AbstractEvent> Prefix(Random rng) {
        AbstractPlayer player = AbstractDungeon.player;
        if (player == null || player.getCardColor() != ZixiuEnums.ZIXIU_COLOR) {
            return SpireReturn.Continue();
        }
        int act = AbstractDungeon.actNum;
        if (act < 1 || act > 3 || hijackedAct == act) {
            return SpireReturn.Continue();
        }
        if (InscriptionRelic.hasAll() || !JiuZiBei.canSpawn()) {
            return SpireReturn.Continue();
        }

        // 这一幕第几个事件房
        if (roomsAct != act) {
            roomsAct = act;
            roomsThisAct = 0;
        }
        roomsThisAct++;

        // 别总砸在第一个事件房上：第一个房间只有一半概率，第二个房间必定触发。
        // （圣主其实也是「条件满足就劫持」，实际效果就是砸在当幕第一个符合条件的事件房，
        //   这里稍微放缓一点，读起来没那么生硬。）
        if (roomsThisAct == 1 && !rng.randomBoolean(0.5F)) {
            logger.info("[zixiu] event hijack skipped first room (act=" + act + ")，下一个事件房再试");
            return SpireReturn.Continue();
        }
        hijackedAct = act;
        logger.info("[zixiu] event hijack -> JiuZiBei (act=" + act + ", room=" + roomsThisAct + ")");
        return SpireReturn.Return(new JiuZiBei());
    }
}
