package zixiu.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import zixiu.ZixiuMod;
import zixiu.relics.CanBei;
import zixiu.relics.InscriptionRelic;

import java.util.ArrayList;

/**
 * 九字发放：奖励界面生成后，往遗物奖励里补一个缺失的九字。
 *
 * 与圣主寻符器同一个挂载点（CombatRewardScreen.setupItemReward），已经带有一条九字奖励时不再插入。
 *
 * <ul>
 *   <li>带着「残碑」：每次有遗物奖励都补一个；</li>
 *   <li>Boss 的遗物奖励：不看残碑，砚秋每个 Boss 保底一个；</li>
 *   <li>攒到 {@link #PITY_AT} 个字却还没摸到残碑：这次遗物奖励里塞一块残碑。</li>
 * </ul>
 */
@SpirePatch(clz = CombatRewardScreen.class, method = "setupItemReward")
public class InscriptionRewardPatch {

    /** 攒到几个字还没残碑，就保底发残碑。 */
    private static final int PITY_AT = 2;

    /** 本局是否已经靠「精英保底」给过一件非九字的字修遗物。 */
    private static boolean classRelicGivenThisRun;

    /** 新开一局时清空。 */
    public static void resetRun() {
        classRelicGivenThisRun = false;
    }

    /** 非九字的字修遗物（初始的秃笔/连书、以及九字驱动的残碑不算在内）。 */
    private static final String[] CLASS_RELICS = {
            "ZanYan", "HuoZheZi", "JiuShuQian", "ShuXia", "ZhuBi", "HuoZiPan"
    };

    /**
     * 必须是 **Prefix**：{@code setupItemReward()} 第一件事就是把 {@code room.rewards}
     * 拷进 {@code this.rewards}，之后界面只认那份副本。Postfix 里往 room.rewards 里塞东西
     * 永远不会显示（圣主的「寻符器」也是挂在 Prefix 上）。
     */
    @SpirePrefixPatch
    public static void Prefix(CombatRewardScreen __instance) {
        AbstractPlayer player = AbstractDungeon.player;
        if (player == null) {
            return;
        }
        ArrayList<RewardItem> rewards;
        try {
            com.megacrit.cardcrawl.rooms.AbstractRoom room = AbstractDungeon.getCurrRoom();
            rewards = room == null ? null : room.rewards;
        } catch (NullPointerException e) {
            // 没有地图节点时 getCurrRoom() 会抛 NPE，奖励界面不该在这种时候出现，直接跳过
            return;
        }
        if (rewards == null || rewards.isEmpty()) {
            return;
        }
        boolean isBossRoom = AbstractDungeon.getCurrRoom()
                instanceof com.megacrit.cardcrawl.rooms.MonsterRoomBoss;

        boolean hasRelicReward = false;
        boolean hasInscription = false;
        for (RewardItem item : rewards) {
            if (item.type != RewardItem.RewardType.RELIC) {
                continue;
            }
            // 注意：Boss 的遗物奖励是「宝箱」项，relic 字段是 null（点开才让你三选一）。
            // 之前这里要求 relic != null，结果 Boss 房一次都没命中保底。
            hasRelicReward = true;
            if (item.relic != null && isInscription(item.relic)) {
                hasInscription = true;
            }
        }
        if (isBossRoom) {
            // Boss 房一定给遗物（宝箱），不管那一条是不是 null 的宝箱项
            hasRelicReward = true;
        }
        if (!hasRelicReward || hasInscription) {
            return;
        }
        boolean zixiuPlayer = player.getCardColor() == zixiu.character.ZixiuEnums.ZIXIU_COLOR;
        boolean wantInscription = player.hasRelic(ZixiuMod.makeID(CanBei.ID))
                || (isBossRoom && zixiuPlayer);
        if (wantInscription) {
            // Boss 保底从「九字 + 普通字修遗物」里混着挑，免得九字来得太快；
            // 残碑触发时仍然只给九字。
            AbstractRelic missing = isBossRoom && !player.hasRelic(ZixiuMod.makeID(CanBei.ID))
                    ? randomBossPityRelic(player)
                    : CanBei.randomMissing();
            if (missing != null) {
                rewards.add(new RewardItem(missing));
                org.apache.logging.log4j.LogManager.getLogger(InscriptionRewardPatch.class)
                        .info("[zixiu] grant inscription " + missing.relicId
                                + (isBossRoom ? " (boss pity)" : " (canbei)"));
                if (player.hasRelic(ZixiuMod.makeID(CanBei.ID))) {
                    player.getRelic(ZixiuMod.makeID(CanBei.ID)).flash();
                }
                return;
            }
        }

        // 精英保底：本局第一次打赢精英时，补一件还没拿过的字修遗物
        // （遗物池被原版 + 其他 mod 稀释得厉害，光靠抽一年也见不到几件）
        boolean isEliteRoom = AbstractDungeon.getCurrRoom()
                instanceof com.megacrit.cardcrawl.rooms.MonsterRoomElite;
        if (isEliteRoom && zixiuPlayer && !classRelicGivenThisRun) {
            AbstractRelic classRelic = randomClassRelic(player);
            if (classRelic != null) {
                rewards.add(new RewardItem(classRelic));
                classRelicGivenThisRun = true;
                org.apache.logging.log4j.LogManager.getLogger(InscriptionRewardPatch.class)
                        .info("[zixiu] grant class relic " + classRelic.relicId + " (elite pity)");
                return;
            }
        }

        // 小保底：攒够了字还没有残碑，就把残碑塞进这次遗物奖励
        if (InscriptionRelic.ownedCount() >= PITY_AT && !InscriptionRelic.hasAll()) {
            rewards.add(new RewardItem(new CanBei()));
        }
    }

    /** Boss 保底：从「还没有的九字」和「还没有的普通字修遗物」里混着挑一件。 */
    private static AbstractRelic randomBossPityRelic(AbstractPlayer player) {
        java.util.ArrayList<AbstractRelic> pool = new java.util.ArrayList<AbstractRelic>();
        for (String id : InscriptionRelic.missingIds()) {
            AbstractRelic relic = InscriptionRelic.make(id);
            if (relic != null) {
                pool.add(relic);
            }
        }
        for (String id : CLASS_RELICS) {
            if (player.hasRelic(ZixiuMod.makeID(id))) {
                continue;
            }
            AbstractRelic relic = makeClassRelic(id);
            if (relic != null) {
                pool.add(relic);
            }
        }
        if (pool.isEmpty()) {
            return null;
        }
        return pool.get(AbstractDungeon.relicRng.random(pool.size() - 1));
    }

    /** 从还没拥有的字修遗物里随机挑一件；全都有了就返回 null。 */
    private static AbstractRelic randomClassRelic(AbstractPlayer player) {
        java.util.ArrayList<AbstractRelic> candidates = new java.util.ArrayList<AbstractRelic>();
        for (String id : CLASS_RELICS) {
            if (player.hasRelic(ZixiuMod.makeID(id))) {
                continue;
            }
            AbstractRelic relic = makeClassRelic(id);
            if (relic != null) {
                candidates.add(relic);
            }
        }
        if (candidates.isEmpty()) {
            return null;
        }
        return candidates.get(AbstractDungeon.relicRng.random(candidates.size() - 1));
    }

    private static AbstractRelic makeClassRelic(String id) {
        if ("ZanYan".equals(id)) {
            return new zixiu.relics.ZanYan();
        }
        if ("HuoZheZi".equals(id)) {
            return new zixiu.relics.HuoZheZi();
        }
        if ("JiuShuQian".equals(id)) {
            return new zixiu.relics.JiuShuQian();
        }
        if ("ShuXia".equals(id)) {
            return new zixiu.relics.ShuXia();
        }
        if ("ZhuBi".equals(id)) {
            return new zixiu.relics.ZhuBi();
        }
        if ("HuoZiPan".equals(id)) {
            return new zixiu.relics.HuoZiPan();
        }
        return null;
    }

    private static boolean isInscription(AbstractRelic relic) {
        for (String id : InscriptionRelic.ALL) {
            if (ZixiuMod.makeID(id).equals(relic.relicId)) {
                return true;
            }
        }
        return false;
    }
}
