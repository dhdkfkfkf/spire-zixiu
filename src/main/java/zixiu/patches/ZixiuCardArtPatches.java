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

    private static TextureRegion common;
    private static TextureRegion uncommon;
    private static TextureRegion rare;

    /** 单字牌用方块印章式匾额，按稀有度换贴图（木 / 银 / 金）。 */
    private static TextureRegion banner(AbstractCard.CardRarity rarity) {
        if (rarity == AbstractCard.CardRarity.RARE) {
            if (rare == null) {
                rare = load("banner_rare.png");
            }
            return rare;
        }
        if (rarity == AbstractCard.CardRarity.UNCOMMON) {
            if (uncommon == null) {
                uncommon = load("banner_uncommon.png");
            }
            return uncommon;
        }
        if (common == null) {
            common = load("banner_common.png");
        }
        return common;
    }

    private static TextureRegion load(String name) {
        Texture tex = ImageMaster.loadImage("zixiu/images/cardui/" + name);
        return tex == null ? null : new TextureRegion(tex);
    }

    @SpirePrefixPatch
    public static SpireReturn<Void> Prefix(AbstractCard __instance, SpriteBatch sb, float x, float y) {
        if (!(__instance instanceof ZixiuCard)) {
            return SpireReturn.Continue();
        }
        TextureRegion tex = banner(__instance.rarity);
        if (tex == null) {
            return SpireReturn.Continue();
        }
        TextureAtlas.AtlasRegion ref = ImageMaster.CARD_BANNER_COMMON;
        float scale = __instance.drawScale * Settings.scale;
        // 方块印章：中心直接对准卡名文字的坐标（current_y + 175 * drawScale * Settings.scale）
        float w = 230.0F;
        float h = 70.0F;
        float cx = __instance.current_x;
        float cy = __instance.current_y + 175.0F * scale;
        sb.setColor(Color.WHITE);
        sb.draw(tex,
                cx - w / 2.0F, cy - h / 2.0F,
                w / 2.0F, h / 2.0F,
                w, h,
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
