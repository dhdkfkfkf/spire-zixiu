package zixiu.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
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
 * 残碑：奖励界面生成后，往遗物奖励里补一个缺失的九字。
 *
 * 与圣主寻符器同一个挂载点（CombatRewardScreen.setupItemReward），已经带有一条九字奖励时不再插入。
 *
 * 另外带一个小保底：已经拓到 {@link #PITY_AT} 个字却还没摸到残碑时，
 * 这次战斗的遗物奖励里必出一条残碑 —— 否则九字基本只靠事件给的那一两个，永远集不齐。
 */
@SpirePatch(clz = CombatRewardScreen.class, method = "setupItemReward")
public class InscriptionRewardPatch {

    /** 攒到几个字还没残碑，就保底发残碑。 */
    private static final int PITY_AT = 2;

    @SpirePostfixPatch
    public static void Postfix(CombatRewardScreen __instance) {
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
        boolean hasRelicReward = false;
        boolean hasInscription = false;
        for (RewardItem item : rewards) {
            if (item.type != RewardItem.RewardType.RELIC || item.relic == null) {
                continue;
            }
            hasRelicReward = true;
            if (isInscription(item.relic)) {
                hasInscription = true;
            }
        }
        if (!hasRelicReward || hasInscription) {
            return;
        }

        if (player.hasRelic(ZixiuMod.makeID(CanBei.ID))) {
            AbstractRelic missing = CanBei.randomMissing();
            if (missing == null) {
                return;
            }
            rewards.add(new RewardItem(missing));
            player.getRelic(ZixiuMod.makeID(CanBei.ID)).flash();
            return;
        }

        // 小保底：攒够了字还没有残碑，就把残碑塞进这次遗物奖励
        if (InscriptionRelic.ownedCount() >= PITY_AT && !InscriptionRelic.hasAll()) {
            rewards.add(new RewardItem(new CanBei()));
        }
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
