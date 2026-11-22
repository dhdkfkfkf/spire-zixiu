package zixiu.core;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import zixiu.relics.HuoZheZi;
import zixiu.relics.ShuXia;
import zixiu.relics.ZanYan;
import zixiu.relics.ZhuBi;

/**
 * 遗物与合/拆/灼烧之间的桥。遗物类本身不知道这些动作，统一在这里判。
 */
public final class RelicHooks {

    private RelicHooks() {
    }

    public static void onFusion(AbstractPlayer player) {
        AbstractRelic zhuBi = player.getRelic(ZhuBi.ID);
        if (zhuBi instanceof ZhuBi) {
            ((ZhuBi) zhuBi).onFusion();
        }
        AbstractRelic zanYan = player.getRelic(ZanYan.ID);
        if (zanYan instanceof ZanYan) {
            ((ZanYan) zanYan).onFusion();
        }
    }

    public static void onSplit(AbstractPlayer player) {
        AbstractRelic shuXia = player.getRelic(ShuXia.ID);
        if (shuXia instanceof ShuXia) {
            ((ShuXia) shuXia).onSplit();
        }
    }

    public static int burnBonus(AbstractPlayer player) {
        AbstractRelic huoZheZi = player.getRelic(HuoZheZi.ID);
        if (huoZheZi instanceof HuoZheZi) {
            return ((HuoZheZi) huoZheZi).consumeBonus();
        }
        return 0;
    }
}
