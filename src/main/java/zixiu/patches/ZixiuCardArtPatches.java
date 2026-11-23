package zixiu.patches;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import zixiu.cards.ZixiuCard;

/**
 * 卡面外观：把字修卡片的匾额换成自绘的漆木金线匾，并按稀有度画角饰。
 *
 * 原版的匾额画在 AbstractCard.renderBannerImage(SpriteBatch, float, float) 里，
 * 这里对字修的牌用 Prefix 跳过原版、画自己的；其他角色的牌照常走原版。
 */
@SpirePatch(clz = AbstractCard.class, method = "renderBannerImage",
        paramtypes = {"com.badlogic.gdx.graphics.g2d.SpriteBatch", "float", "float"})
public class ZixiuCardArtPatches {

    private static Texture banner;

    private static Texture banner() {
        if (banner == null) {
            banner = ImageMaster.loadImage("zixiu/images/cardui/banner.png");
        }
        return banner;
    }

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

    /** 稀有度角饰：稀有=金、罕见=银，普通不加。 */
    @SpirePostfixPatch
    public static void PostfixRender(AbstractCard __instance, SpriteBatch sb) {
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
        float cx = __instance.current_x;
        float cy = __instance.current_y;
        sb.setColor(Color.WHITE);
        for (int sx = -1; sx <= 1; sx += 2) {
            for (int sy = -1; sy <= 1; sy += 2) {
                sb.draw(tex, cx + sx * halfW - size / 2.0F, cy + sy * halfH - size / 2.0F,
                        size, size);
            }
        }
    }

    @SpirePrefixPatch
    public static SpireReturn<Void> Prefix(AbstractCard __instance, SpriteBatch sb, float x, float y) {
        if (!(__instance instanceof ZixiuCard)) {
            return SpireReturn.Continue();
        }
        Texture tex = banner();
        if (tex == null) {
            return SpireReturn.Continue();
        }
        float scale = __instance.drawScale * Settings.scale;
        float w = 324.0F * scale;
        float h = 76.5F * scale;
        // 匾额中心在卡牌中心上方约 152（512 空间）
        float cx = __instance.current_x;
        float cy = __instance.current_y + 152.0F * scale;
        sb.setColor(Color.WHITE);
        sb.draw(tex, cx - w / 2.0F, cy - h / 2.0F, w, h);
        return SpireReturn.Return(null);
    }
}

@SpirePatch(clz = AbstractCard.class, method = "render",
        paramtypes = {"com.badlogic.gdx.graphics.g2d.SpriteBatch"})
class ZixiuCardOrnamentPatch {
    @SpirePostfixPatch
    public static void Postfix(AbstractCard __instance, SpriteBatch sb) {
        ZixiuCardArtPatches.PostfixRender(__instance, sb);
    }
}
