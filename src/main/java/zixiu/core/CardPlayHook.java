package zixiu.core;

import basemod.interfaces.OnCardUseSubscriber;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import zixiu.cards.CardComponents;
import zixiu.powers.CausePower;
import zixiu.powers.ChainPower;
import zixiu.lexicon.ZixiuLexicon;
import zixiu.ZixiuMod;
import zixiu.powers.OsmanthusPower;
import zixiu.powers.YanlingPower;
import zixiu.powers.TaPower;

/**
 * 打出卡牌时的全局钩子：桂（技能牌）、因（成品牌）、九字遗物、真言。
 */
public class CardPlayHook implements OnCardUseSubscriber {

    @Override
    public void receiveCardUsed(AbstractCard card) {
        AbstractPlayer player = AbstractDungeon.player;
        if (player == null) {
            return;
        }
        // 沓：上一张牌的翻倍窗口到此为止；这一张如果是攻击牌，消耗一层并开窗
        TaPower.closeWindow();
        if (card.type == AbstractCard.CardType.ATTACK) {
            TaPower.onAttackCardPlayed(player);
        }
        if (card.type == AbstractCard.CardType.SKILL) {
            AbstractPower gui = player.getPower(OsmanthusPower.POWER_ID);
            if (gui instanceof OsmanthusPower) {
                ((OsmanthusPower) gui).onSkillPlayed();
            }
        }
        String glyph = CardComponents.glyphOf(card);
        // 连书现在是初始遗物的能力，没有这件遗物就不接链
        if (glyph != null && player.hasRelic(ZixiuMod.makeID(zixiu.relics.LianShu.ID))) {
            AbstractPower yin = player.getPower(CausePower.POWER_ID);
            if (yin instanceof CausePower) {
                ((CausePower) yin).onWordPlayed();
            }
            int chain = FusionTracker.recordWord(ZixiuLexicon.partsOf(glyph), glyph);
            ChainPower.sync();
            if (chain > 0) {
                applyChainDamage(player, chain);
                zixiu.powers.MiaoPower.onChain(player, chain);
                AbstractDungeon.effectList.add(new zixiu.vfx.ChainEffect(
                        "连书 ×" + chain, player.hb.cX, player.hb.cY + 140.0F * Settings.scale));
                CardCrawlGame.sound.play("ATTACK_FIRE");
            }
        }
        RelicHooks.onCardPlayed(player, card);
        // 九字真言·言灵：集齐后每张牌按组成单位数轮转并触发一次
        YanlingPower.onCardPlayed(player, card);
        // 言灵「在」：上一轮给的减费在这里结算（牌已付费，返还 1 点）
        if (YanlingPower.consumeDiscount(card)) {
            AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(1));
        }
    }

    /** 连书：第 n 连对所有敌人造成 2n 点伤害。 */
    private static void applyChainDamage(AbstractPlayer player, int chain) {
        if (AbstractDungeon.getMonsters() == null) {
            return;
        }
        for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
            if (monster == null || monster.isDeadOrEscaped()) {
                continue;
            }
            AbstractDungeon.actionManager.addToBottom(new DamageAction(monster,
                    new DamageInfo(player, 2 * chain, DamageInfo.DamageType.THORNS),
                    AbstractGameAction.AttackEffect.FIRE));
        }
    }

}
