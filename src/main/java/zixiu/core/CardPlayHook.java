package zixiu.core;

import basemod.interfaces.OnCardUseSubscriber;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import zixiu.cards.CardComponents;
import zixiu.powers.CausePower;
import zixiu.powers.OsmanthusPower;
import zixiu.relics.InscriptionRelic;

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
        if (card.type == AbstractCard.CardType.SKILL) {
            AbstractPower gui = player.getPower(OsmanthusPower.POWER_ID);
            if (gui instanceof OsmanthusPower) {
                ((OsmanthusPower) gui).onSkillPlayed();
            }
        }
        if (CardComponents.glyphOf(card) != null) {
            AbstractPower yin = player.getPower(CausePower.POWER_ID);
            if (yin instanceof CausePower) {
                ((CausePower) yin).onWordPlayed();
            }
        }
        RelicHooks.onCardPlayed(player, card);
        if (InscriptionRelic.hasAll()) {
            applyTrueWord(player);
        }
    }

    /** 真言：集齐九字后，每打出一张牌，所有敌人获得 1 层虚弱与 1 层易伤。 */
    private static void applyTrueWord(AbstractPlayer player) {
        if (AbstractDungeon.getMonsters() == null) {
            return;
        }
        for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
            if (monster == null || monster.isDeadOrEscaped()) {
                continue;
            }
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(monster, player,
                    new WeakPower(monster, 1, false), 1));
            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(monster, player,
                    new VulnerablePower(monster, 1, false), 1));
        }
    }
}
