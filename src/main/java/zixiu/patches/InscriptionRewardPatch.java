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
 */
@SpirePatch(clz = CombatRewardScreen.class, method = "setupItemReward")
public class InscriptionRewardPatch {

    @SpirePostfixPatch
    public static void Postfix(CombatRewardScreen __instance) {
        AbstractPlayer player = AbstractDungeon.player;
        if (player == null || !player.hasRelic(ZixiuMod.makeID(CanBei.ID))) {
            return;
        }
        ArrayList<RewardItem> rewards = AbstractDungeon.getCurrRoom() == null
                ? null : AbstractDungeon.getCurrRoom().rewards;
        if (rewards == null || rewards.isEmpty()) {
            return;
        }
        boolean hasRelicReward = false;
        for (RewardItem item : rewards) {
            if (item.type != RewardItem.RewardType.RELIC || item.relic == null) {
                continue;
            }
            hasRelicReward = true;
            if (isInscription(item.relic)) {
                return;
            }
        }
        if (!hasRelicReward) {
            return;
        }
        AbstractRelic missing = CanBei.randomMissing();
        if (missing == null) {
            return;
        }
        rewards.add(new RewardItem(missing));
        player.getRelic(ZixiuMod.makeID(CanBei.ID)).flash();
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
