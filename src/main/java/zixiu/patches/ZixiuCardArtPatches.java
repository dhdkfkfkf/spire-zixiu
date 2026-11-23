package zixiu.patches;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import zixiu.cards.ZixiuCard;

/**
 * 卡面外观：字修卡片的匾额换成自绘漆木金线匾，并按稀有度画四角饰。
 *
 * 匾额完全复刻原版 renderHelper 的放置公式（读原版匾额区域的 original/offset/packed），
 * 这样小卡、大图预览、牌库任何缩放下都和原版对齐。只对 ZixiuCard 生效。
 */
@SpirePatch(clz = AbstractCard.class, method = "renderBannerImage",
        paramtypes = {"com.badlogic.gdx.graphics.g2d.SpriteBatch", "float", "float"})
public class ZixiuCardArtPatches {

    private static TextureRegion banner;

    private static TextureRegion banner() {
        if (banner == null) {
            Texture tex = ImageMaster.loadImage("zixiu/images/cardui/banner.png");
            if (tex != null) {
                banner = new TextureRegion(tex);
            }
        }
        return banner;
    }

    @SpirePrefixPatch
    public static SpireReturn<Void> Prefix(AbstractCard __instance, SpriteBatch sb, float x, float y) {
        if (!(__instance instanceof ZixiuCard)) {
            return SpireReturn.Continue();
        }
        TextureRegion tex = banner();
        if (tex == null) {
            return SpireReturn.Continue();
        }
        TextureAtlas.AtlasRegion ref = ImageMaster.CARD_BANNER_COMMON;
        float scale = __instance.drawScale * Settings.scale;
        sb.setColor(Color.WHITE);
        sb.draw(tex,
                x + ref.offsetX - ref.originalWidth / 2.0F,
                y + ref.offsetY - ref.originalHeight / 2.0F,
                ref.originalWidth / 2.0F - ref.offsetX,
                ref.originalHeight / 2.0F - ref.offsetY,
                ref.packedWidth,
                ref.packedHeight,
                scale, scale, __instance.angle);
        return SpireReturn.Return(null);
    }
}

@SpirePatch(clz = AbstractCard.class, method = "render",
        paramtypes = {"com.badlogic.gdx.graphics.g2d.SpriteBatch"})
class ZixiuCardOrnamentPatch {

    private static Texture gold;
    private static Texture silver;

    private static Texture ornament(AbstractCard.CardRarity rarity) {
        if (rarity == AbstractCard.CardRarity.RARE) {
            if (gold == null) {
                gold = ImageMaster.loadImage("zixiu/images/cardui/orn_gold.png");
            }
            return gold;
        }
        if (rarity == AbstractCard.CardRarity.UNCOMMON) {
            if (silver == null) {
                silver = ImageMaster.loadImage("zixiu/images/cardui/orn_silver.png");
            }
            return silver;
        }
        return null;
    }

    /** 稀有度角饰：稀有=金、罕见=银、普通不加。 */
    @SpirePostfixPatch
    public static void Postfix(AbstractCard __instance, SpriteBatch sb) {
        if (!(__instance instanceof ZixiuCard)) {
            return;
        }
        Texture tex = ornament(__instance.rarity);
        if (tex == null) {
            return;
        }
        float scale = __instance.drawScale * Settings.scale;
        float halfW = 108.0F * scale;
        float halfH = 168.0F * scale;
        float size = 26.0F * scale;
        sb.setColor(Color.WHITE);
        for (int sx = -1; sx <= 1; sx += 2) {
            for (int sy = -1; sy <= 1; sy += 2) {
                sb.draw(tex,
                        __instance.current_x + sx * halfW - size / 2.0F,
                        __instance.current_y + sy * halfH - size / 2.0F,
                        size, size);
            }
        }
    }
}
