package zixiu.core;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import zixiu.ZixiuMod;
import zixiu.relics.HuoZheZi;
import zixiu.relics.InscriptionRelic;
import zixiu.relics.ShuXia;
import zixiu.relics.ZanYan;
import zixiu.relics.ZhuBi;

/**
 * 遗物与合/拆/打出之间的桥。遗物类本身不知道这些动作，统一在这里判。
 */
public final class RelicHooks {

    private RelicHooks() {
    }

    public static void onFusion(AbstractPlayer player) {
        AbstractRelic zhuBi = player.getRelic(ZixiuMod.makeID(ZhuBi.ID));
        if (zhuBi instanceof ZhuBi) {
            ((ZhuBi) zhuBi).onFusion();
        }
        AbstractRelic zanYan = player.getRelic(ZixiuMod.makeID(ZanYan.ID));
        if (zanYan instanceof ZanYan) {
            ((ZanYan) zanYan).onFusion();
        }
        for (AbstractRelic relic : player.relics) {
            if (relic instanceof InscriptionRelic) {
                ((InscriptionRelic) relic).onFusion();
            }
        }
    }

    public static void onSplit(AbstractPlayer player) {
        AbstractRelic shuXia = player.getRelic(ZixiuMod.makeID(ShuXia.ID));
        if (shuXia instanceof ShuXia) {
            ((ShuXia) shuXia).onSplit();
        }
        for (AbstractRelic relic : player.relics) {
            if (relic instanceof InscriptionRelic) {
                ((InscriptionRelic) relic).onSplit();
            }
        }
    }

    public static void onCardPlayed(AbstractPlayer player, AbstractCard card) {
        for (AbstractRelic relic : player.relics) {
            if (relic instanceof InscriptionRelic) {
                ((InscriptionRelic) relic).onCardPlayed(card);
            }
        }
    }

    public static int burnBonus(AbstractPlayer player) {
        AbstractRelic huoZheZi = player.getRelic(ZixiuMod.makeID(HuoZheZi.ID));
        if (huoZheZi instanceof HuoZheZi) {
            return ((HuoZheZi) huoZheZi).consumeBonus();
        }
        return 0;
    }
}
