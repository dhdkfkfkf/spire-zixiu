package zixiu.patches;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zixiu.cards.ZixiuCard;

/**
 * 稀有度描边：卡牌外沿再叠一圈色带，颜色沿用原版匾额体系
 * （普通银灰、罕见青、稀有金、部件牌青玉）。
 *
 * 用渲染前缀补丁而不是逐卡换底图：卡片转多少度、缩放多少，描边就跟着转多少、缩多少，
 * 不会在卡牌大图里出现「小底图 + 大卡面」叠影。
 *
 * 画在卡牌**之前**、贴在卡身轮廓**外面**，所以卡面上的任何东西都不会被压住——
 * 匾额与费用球就贴在卡顶边缘，画在卡面上一定会盖到它们。
 *
 * 两条渲染路径都要挂：
 * <ul>
 *   <li>{@code render(sb, boolean)}——手牌、奖励、牌堆都走它（单参数的 render 转调它）；</li>
 *   <li>{@code renderInLibrary(sb)}——卡牌库/图鉴走它，其中「看升级版」分支会再转调
 *       render(sb)，那边已经画过，这里跳过，免得叠两层。</li>
 * </ul>
 * 单卡大图（SingleCardViewPopup）自己逐层贴图，不经过卡牌对象的 render，本补丁不覆盖。
 */
public class ZixiuFrameOverlay {

    private static final Logger logger = LogManager.getLogger(ZixiuFrameOverlay.class);

    private static final String RING_IMG = "zixiu/images/cardui/frame_ring.png";

    private static final Color COMMON = new Color(0.78F, 0.78F, 0.80F, 1.0F);
    private static final Color UNCOMMON = new Color(0.34F, 0.73F, 0.84F, 1.0F);
    private static final Color RARE = new Color(0.79F, 0.64F, 0.36F, 1.0F);
    private static final Color SPECIAL = new Color(0.43F, 0.62F, 0.52F, 1.0F);

    private static Texture ring;
    private static boolean ringLoaded;
    private static boolean firstDrawLogged;

    @SpirePatch(clz = AbstractCard.class, method = "render",
            paramtypes = {"com.badlogic.gdx.graphics.g2d.SpriteBatch", "boolean"})
    public static class Render {
        @SpirePrefixPatch
        public static void Prefix(AbstractCard card, SpriteBatch sb, boolean hovered) {
            draw(sb, card);
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "renderInLibrary",
            paramtypes = {"com.badlogic.gdx.graphics.g2d.SpriteBatch"})
    public static class RenderInLibrary {
        @SpirePrefixPatch
        public static void Prefix(AbstractCard card, SpriteBatch sb) {
            // 「看升级版」这一支内部 makeCopy 后调 render(sb)，描边由 Render 画过
            if (SingleCardViewPopup.isViewingUpgrade && card.isSeen && !card.isLocked) {
                return;
            }
            draw(sb, card);
        }
    }

    private static void draw(SpriteBatch sb, AbstractCard card) {
        if (!(card instanceof ZixiuCard) || Settings.hideCards) {
            return;
        }
        float alpha = card.transparency;
        if (alpha <= 0.05F) {
            return;
        }
        Texture texture = ring();
        if (texture == null) {
            return;
        }
        // 与 BaseMod 画卡底一致：512 的框 × drawScale × UI 缩放
        float size = 512.0F * card.drawScale * Settings.scale;
        // renderInLibrary 对屏幕外的卡会提前 return，这里补一个范围判断，免得在屏幕边缘留下孤零零的描边
        if (card.current_x + size < 0.0F || card.current_x - size > Settings.WIDTH
                || card.current_y + size < 0.0F || card.current_y - size > Settings.HEIGHT) {
            return;
        }
        Color color = colorFor(card.rarity);
        sb.setColor(color.r, color.g, color.b, color.a * alpha);
        sb.draw(texture,
                card.current_x - size / 2.0F, card.current_y - size / 2.0F,
                size / 2.0F, size / 2.0F,
                size, size,
                1.0F, 1.0F, card.angle,
                0, 0, texture.getWidth(), texture.getHeight(),
                false, false);
        sb.setColor(Color.WHITE);

        if (!firstDrawLogged) {
            firstDrawLogged = true;
            logger.info("[zixiu] frame overlay first draw: " + card.cardID + " rarity=" + card.rarity);
        }
    }

    /**
     * 原版稀有度「动态框」——悬停/大图时才画的那层金银凸边，是照原版卡框裁的。
     * 字修卡用的是自绘卡框，这层凸边（尤其它的黑描边）会错位压在卡面上；
     * 字修卡不需要它，直接跳过。
     */
    @SpirePatch(clz = AbstractCard.class, method = "renderDynamicFrame",
            paramtypes = {"com.badlogic.gdx.graphics.g2d.SpriteBatch",
                    "float", "float", "float", "float"})
    public static class DynamicFrame {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(AbstractCard card, SpriteBatch sb,
                                               float x, float y, float a, float b) {
            return card instanceof ZixiuCard ? SpireReturn.Return(null) : SpireReturn.Continue();
        }
    }

    /** 单卡大图里的同一层凸边，单独挂一次。 */
    @SpirePatch(clz = SingleCardViewPopup.class, method = "renderDynamicFrame",
            paramtypes = {"com.badlogic.gdx.graphics.g2d.SpriteBatch",
                    "float", "float", "float", "float"})
    public static class SingleViewDynamicFrame {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(SingleCardViewPopup popup, SpriteBatch sb,
                                               float x, float y, float a, float b) {
            AbstractCard card = popupCard(popup);
            return card instanceof ZixiuCard ? SpireReturn.Return(null) : SpireReturn.Continue();
        }
    }

    /** SingleCardViewPopup.card 是 private 且没有 getter，反射读一次就缓存住。 */
    private static java.lang.reflect.Field scvCardField;
    private static boolean scvCardLookedUp;

    private static AbstractCard popupCard(SingleCardViewPopup popup) {
        if (!scvCardLookedUp) {
            scvCardLookedUp = true;
            try {
                scvCardField = SingleCardViewPopup.class.getDeclaredField("card");
                scvCardField.setAccessible(true);
            } catch (Exception e) {
                logger.warn("[zixiu] 取不到 SingleCardViewPopup.card，大图的动态框去不掉了：" + e);
            }
        }
        if (scvCardField == null) {
            return null;
        }
        try {
            return (AbstractCard) scvCardField.get(popup);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    private static Texture ring() {
        if (!ringLoaded) {
            ringLoaded = true;
            ring = ImageMaster.loadImage(RING_IMG);
            // 贴图缺失时 loadImage 自己会打 [WARNING] No image at ...，这里只补一条加载结果
            logger.info("[zixiu] frame ring texture "
                    + (ring == null ? "MISSING" : ring.getWidth() + "x" + ring.getHeight()));
        }
        return ring;
    }

    private static Color colorFor(AbstractCard.CardRarity rarity) {
        if (rarity == AbstractCard.CardRarity.UNCOMMON) {
            return UNCOMMON;
        }
        if (rarity == AbstractCard.CardRarity.RARE) {
            return RARE;
        }
        if (rarity == AbstractCard.CardRarity.SPECIAL) {
            return SPECIAL;
        }
        return COMMON;
    }
}
