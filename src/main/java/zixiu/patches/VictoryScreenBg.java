package zixiu.patches;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.VictoryScreen;
import zixiu.character.ZixiuCharacter;

/**
 * 通关后的「胜利!」统计界面：原版这一屏自己不画背景，只把房间清成黑底，
 * 所以除了胜利横幅和按钮什么都没有。
 *
 * <p>砚秋通关时把专用的胜利背景（清晨的街市，回家那张）铺在下面，并压一层半透明黑，
 * 保证统计文字可读。三格通关动画仍然用它自己的 {@code yanqiu_bg.jpg}（书房），两张互不干扰。
 *
 * <p>注意：不能用 {@code AbstractPlayer.renderShoulderImg} 那条路——原版只在
 * 「篝火房间」和「静谧（Silent）通关」两种情况下才会调它，砚秋这屏根本走不到。
 */
@SpirePatch(clz = VictoryScreen.class, method = "render",
        paramtypes = {"com.badlogic.gdx.graphics.g2d.SpriteBatch"})
public class VictoryScreenBg {

    private static final String BG = "zixiu/images/scenes/victory_bg.jpg";
    private static final String FALLBACK = "zixiu/images/char/shoulder.png";
    private static final float VEIL_ALPHA = 0.4F;

    private static Texture bg;
    private static boolean tried;

    @SpirePrefixPatch
    public static void Prefix(VictoryScreen screen, SpriteBatch sb) {
        if (!(AbstractDungeon.player instanceof ZixiuCharacter)) {
            return;
        }
        if (!tried) {
            tried = true;
            bg = ImageMaster.loadImage(BG);
            if (bg == null) {
                bg = ImageMaster.loadImage(FALLBACK);
            }
        }
        if (bg == null) {
            return;
        }

        float width = Settings.WIDTH;
        float height = width * (bg.getHeight() / (float) bg.getWidth());
        sb.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        sb.draw(bg, 0.0F, (Settings.HEIGHT - height) / 2.0F, width, height);

        sb.setColor(0.0F, 0.0F, 0.0F, VEIL_ALPHA);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, Settings.WIDTH, Settings.HEIGHT);
        sb.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
