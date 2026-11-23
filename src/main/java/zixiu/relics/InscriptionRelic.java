package zixiu.relics;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import zixiu.ZixiuMod;
import zixiu.core.ZixiuConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 九字真言遗物：临兵斗者皆阵列前行。
 *
 * 九个字都是一件 SPECIAL 遗物（不进任何遗物池），只能通过「九字碑」事件或「残碑」遗物获得。
 * 隔离开启时只进字修的专属池，普通角色看不到；关闭隔离后进共享池，任何角色都能掉到。
 */
public abstract class InscriptionRelic extends ZixiuRelic {

    public static final List<String> ALL = Arrays.asList(
            "Lin", "Bing", "Dou", "Zhe", "Jie", "Zhen", "Lie", "Qian", "Xing");

    protected InscriptionRelic(String id, String img) {
        super(ZixiuMod.makeID(id), img,
                ZixiuConfig.isolation() ? RelicTier.SPECIAL : RelicTier.RARE,
                LandingSound.FLAT);
    }

    /** 玩家拆解一张牌之后。 */
    public void onSplit() {
    }

    /** 玩家合出一个字之后。 */
    public void onFusion() {
    }

    /** 玩家打出一张牌之后。 */
    public void onCardPlayed(AbstractCard card) {
    }

    public static boolean owned(String id) {
        return AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(ZixiuMod.makeID(id));
    }

    public static int ownedCount() {
        if (AbstractDungeon.player == null) {
            return 0;
        }
        int count = 0;
        for (String id : ALL) {
            if (owned(id)) {
                count++;
            }
        }
        return count;
    }

    public static boolean hasAll() {
        return ownedCount() == ALL.size();
    }

    public static List<String> missingIds() {
        List<String> missing = new ArrayList<String>();
        for (String id : ALL) {
            if (!owned(id)) {
                missing.add(id);
            }
        }
        return missing;
    }

    public static AbstractRelic make(String id) {
        if ("Lin".equals(id)) {
            return new Lin();
        }
        if ("Bing".equals(id)) {
            return new Bing();
        }
        if ("Dou".equals(id)) {
            return new Dou();
        }
        if ("Zhe".equals(id)) {
            return new Zhe();
        }
        if ("Jie".equals(id)) {
            return new Jie();
        }
        if ("Zhen".equals(id)) {
            return new Zhen();
        }
        if ("Lie".equals(id)) {
            return new Lie();
        }
        if ("Qian".equals(id)) {
            return new Qian();
        }
        if ("Xing".equals(id)) {
            return new Xing();
        }
        return null;
    }
}
