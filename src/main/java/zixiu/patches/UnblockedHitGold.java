package zixiu.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import zixiu.powers.DuPower;

/**
 * 「杜」的判定：怪物每次挨完伤害之后，看 lastDamageTaken（扣掉格挡后真正掉的血）。
 *
 * 只算攻击伤害（DamageType.NORMAL）——灼烧/连书/因 这些 THORNS、HP_LOSS 不算「攻击」。
 */
@SpirePatch(clz = AbstractMonster.class, method = "damage",
        paramtypes = {"com.megacrit.cardcrawl.cards.DamageInfo"})
public class UnblockedHitGold {

    @SpirePostfixPatch
    public static void Postfix(AbstractMonster __instance, DamageInfo info) {
        if (__instance == null || info == null) {
            return;
        }
        if (info.type != DamageInfo.DamageType.NORMAL) {
            return;
        }
        if (__instance.lastDamageTaken <= 0) {
            // 全被格挡，不算
            return;
        }
        DuPower.onUnblockedAttack();
    }
}
