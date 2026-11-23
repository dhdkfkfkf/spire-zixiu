package zixiu.core;

import basemod.interfaces.StartGameSubscriber;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import zixiu.ZixiuMod;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 开局把遗物池与事件表的自检结果写到游戏目录的 zixiu-diag.txt。
 *
 * 用来回答「为什么一局下来一个本模组遗物/事件都没见到」：如果这里显示 ID 不在池里，
 * 就是注册链路的问题；如果在池里，那就是概率。
 */
public final class PoolDiagnostics implements StartGameSubscriber {

    private static final String[] POOL_RELICS = {
            "TuBi", "ZanYan", "HuoZheZi", "JiuShuQian", "ShuXia", "ZhuBi", "HuoZiPan", "CanBei"
    };
    private static final String[] EVENTS = {"CuoZi", "CanJuan", "JiuZiBei"};

    @Override
    public void receiveStartGame() {
        dump();
    }

    private static void dump() {
        if (AbstractDungeon.player == null) {
            return;
        }
        PrintWriter writer = null;
        try {
            File file = new File("zixiu-diag.txt");
            writer = new PrintWriter(new OutputStreamWriter(
                    new FileOutputStream(file), StandardCharsets.UTF_8));
            writer.println("zixiu diagnostics");
            writer.println("chosenClass = " + AbstractDungeon.player.chosenClass);
            writer.println("cardColor   = " + AbstractDungeon.player.getCardColor());
            writer.println("actNum      = " + AbstractDungeon.actNum);
            writer.println();
            writer.println("relic pool sizes: common=" + size(AbstractDungeon.commonRelicPool)
                    + " uncommon=" + size(AbstractDungeon.uncommonRelicPool)
                    + " rare=" + size(AbstractDungeon.rareRelicPool)
                    + " shop=" + size(AbstractDungeon.shopRelicPool)
                    + " boss=" + size(AbstractDungeon.bossRelicPool));
            writer.println();
            for (String id : POOL_RELICS) {
                String full = ZixiuMod.makeID(id);
                writer.println(full
                        + " common=" + in(AbstractDungeon.commonRelicPool, full)
                        + " uncommon=" + in(AbstractDungeon.uncommonRelicPool, full)
                        + " rare=" + in(AbstractDungeon.rareRelicPool, full)
                        + " shop=" + in(AbstractDungeon.shopRelicPool, full)
                        + " boss=" + in(AbstractDungeon.bossRelicPool, full));
            }
            writer.println();
            List<String> events = AbstractDungeon.eventList == null
                    ? new ArrayList<String>() : AbstractDungeon.eventList;
            writer.println("eventList size = " + events.size());
            for (String id : EVENTS) {
                String full = ZixiuMod.makeID(id);
                writer.println(full + " inEventList=" + events.contains(full));
            }
            writer.println();
            writer.println("eventList contents:");
            for (String id : events) {
                writer.println("  " + id);
            }
        } catch (Exception e) {
            if (writer != null) {
                writer.println("dump failed: " + e);
            }
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private static int size(List<String> list) {
        return list == null ? -1 : list.size();
    }

    private static boolean in(List<String> list, String id) {
        return list != null && list.contains(id);
    }
}
