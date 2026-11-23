package zixiu.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import zixiu.lexicon.Component;
import zixiu.lexicon.ZixiuLexicon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 卡牌与字学核心之间的适配层：把一张手牌展开成底层部件，或按字造一张卡。
 */
public final class CardComponents {

    private static final Map<String, Supplier<AbstractCard>> FACTORY =
            new LinkedHashMap<String, Supplier<AbstractCard>>();
    private static final Map<Class<?>, String> GLYPHS = new HashMap<Class<?>, String>();

    static {
        // 闭环样品
        register(Dai.GLYPH, Dai::new, Dai.class);
        register(Xing.GLYPH, Xing::new, Xing.class);
        register(Kun.GLYPH, Kun::new, Kun.class);
        register(Shu.GLYPH, Shu::new, Shu.class);
        register(Sen.GLYPH, Sen::new, Sen.class);

        // 焚
        register(Yan.GLYPH, Yan::new, Yan.class);
        register(Can.GLYPH, Can::new, Can.class);
        register(Er.GLYPH, Er::new, Er.class);
        register(Zao.GLYPH, Zao::new, Zao.class);
        register(Da.GLYPH, Da::new, Da.class);
        register(Pin.GLYPH, Pin::new, Pin.class);
        register(YanYan.GLYPH, YanYan::new, YanYan.class);
        register(Beng.GLYPH, Beng::new, Beng.class);
        register(Fu.GLYPH, Fu::new, Fu.class);
        register(Wang.GLYPH, Wang::new, Wang.class);
        register(Zhong.GLYPH, Zhong::new, Zhong.class);
        register(Fen.GLYPH, Fen::new, Fen.class);
        register(Mie.GLYPH, Mie::new, Mie.class);
        register(Jie.GLYPH, Jie::new, Jie.class);
        register(Jiong.GLYPH, Jiong::new, Jiong.class);
        register(Cong.GLYPH, Cong::new, Cong.class);
        register(Companion.GLYPH, Companion::new, Companion.class);
        register(Mo.GLYPH, Mo::new, Mo.class);
        register(Chu.GLYPH, Chu::new, Chu.class);
        register(Dan.GLYPH, Dan::new, Dan.class);

        // 润
        register(Bathe.GLYPH, Bathe::new, Bathe.class);
        register(Blockade.GLYPH, Blockade::new, Blockade.class);
        register(Grove.GLYPH, Grove::new, Grove.class);
        register(Body.GLYPH, Body::new, Body.class);
        register(Guard.GLYPH, Guard::new, Guard.class);
        register(Drizzle.GLYPH, Drizzle::new, Drizzle.class);
        register(Thicket.GLYPH, Thicket::new, Thicket.class);
        register(Benevolence.GLYPH, Benevolence::new, Benevolence.class);
        register(Belly.GLYPH, Belly::new, Belly.class);
        register(Bland.GLYPH, Bland::new, Bland.class);
        register(Osmanthus.GLYPH, Osmanthus::new, Osmanthus.class);
        register(Hollow.GLYPH, Hollow::new, Hollow.class);
        register(Mound.GLYPH, Mound::new, Mound.class);

        // 照
        register(Bright.GLYPH, Bright::new, Bright.class);
        register(Prosper.GLYPH, Prosper::new, Prosper.class);
        register(Dawn.GLYPH, Dawn::new, Dawn.class);
        register(Lucky.GLYPH, Lucky::new, Lucky.class);
        register(Fine.GLYPH, Fine::new, Fine.class);
        register(But.GLYPH, But::new, But.class);
        register(Crystal.GLYPH, Crystal::new, Crystal.class);
        register(Noon.GLYPH, Noon::new, Noon.class);
        register(Cause.GLYPH, Cause::new, Cause.class);
        register(Immortal.GLYPH, Immortal::new, Immortal.class);
        register(Trio.GLYPH, Trio::new, Trio.class);
        register(Forever.GLYPH, Forever::new, Forever.class);
        register(Dim.GLYPH, Dim::new, Dim.class);
        register(Whole.GLYPH, Whole::new, Whole.class);
        register(Sky.GLYPH, Sky::new, Sky.class);
        register(Wei.GLYPH, Wei::new, Wei.class);
        register(Chang.GLYPH, Chang::new, Chang.class);
        register(Cha.GLYPH, Cha::new, Cha.class);

        // 通用
        register(Qiu.GLYPH, Qiu::new, Qiu.class);
        register(Xiu.GLYPH, Xiu::new, Xiu.class);
        register(Shi.GLYPH, Shi::new, Shi.class);
        register(Hui.GLYPH, Hui::new, Hui.class);
        register(Lu.GLYPH, Lu::new, Lu.class);
        register(Ben.GLYPH, Ben::new, Ben.class);
        register(Renew.GLYPH, Renew::new, Renew.class);
        register(Peng.GLYPH, Peng::new, Peng.class);
        register(Qie.GLYPH, Qie::new, Qie.class);
        register(He.GLYPH, He::new, He.class);
        register(Zuo.GLYPH, Zuo::new, Zuo.class);
    }

    private CardComponents() {
    }

    private static void register(String glyph, Supplier<AbstractCard> factory, Class<?> type) {
        FACTORY.put(glyph, factory);
        GLYPHS.put(type, glyph);
    }

    public static List<Component> expand(AbstractCard card) {
        if (card instanceof ComponentCard) {
            return java.util.Collections.singletonList(((ComponentCard) card).component());
        }
        String glyph = glyphOf(card);
        if (glyph != null) {
            return ZixiuLexicon.partsOf(glyph);
        }
        return java.util.Collections.emptyList();
    }

    public static String glyphOf(AbstractCard card) {
        if (card instanceof ComponentCard) {
            return null;
        }
        return GLYPHS.get(card.getClass());
    }

    public static AbstractCard create(String glyph) {
        Supplier<AbstractCard> factory = FACTORY.get(glyph);
        return factory == null ? null : factory.get();
    }

    public static AbstractCard createComponent(Component component) {
        switch (component) {
            case HUO:
                return new Huo();
            case MU:
                return new Mu();
            case KOU:
                return new Kou();
            case REN:
                return new Ren();
            case YUE:
                return new Yue();
            case TU:
                return new Tu();
            case YI:
                return new Yi();
            case SHUI:
                return new Shui();
            case SHAN:
                return new Shan();
            case RI:
                return new Ri();
            default:
                return null;
        }
    }

    /**
     * 能不能当合字材料。
     *
     * 只认部件牌：成品牌要先拆再合（拆免费且不限次数），这样「合」的选牌界面里只会出现材料，
     * 不会和「拆」的界面混在一起。
     */
    public static boolean isFusable(AbstractCard card) {
        return card instanceof ComponentCard;
    }

    public static List<Component> expandAll(List<AbstractCard> cards) {
        List<Component> parts = new ArrayList<Component>();
        for (AbstractCard card : cards) {
            parts.addAll(expand(card));
        }
        return parts;
    }

    public static int componentCount(List<AbstractCard> cards) {
        int count = 0;
        for (AbstractCard card : cards) {
            if (card instanceof ComponentCard) {
                count++;
            }
        }
        return count;
    }
}
