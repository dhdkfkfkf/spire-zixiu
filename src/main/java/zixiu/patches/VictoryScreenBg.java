package zixiu.patches;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.VictoryScreen;
import zixiu.character.ZixiuEnums;

/**
 * 通关后的「胜利!」统计界面：原版是纯黑底（游戏只清了屏，这一屏自己不画背景）。
 * 砚秋通关时把通关动画的背景垫在下面，别让最后一屏是黑的。
 */
@SpirePatch(clz = VictoryScreen.class, method = "render",
        paramtypes = {"com.badlogic.gdx.graphics.g2d.SpriteBatch"})
public class VictoryScreenBg {

    private static final String BG = "zixiu/images/scenes/yanqiu_bg.jpg";
    private static Texture bg;
    private static boolean tried;

    @SpirePrefixPatch
    public static void Prefix(VictoryScreen screen, SpriteBatch sb) {
        if (AbstractDungeon.player == null
                || AbstractDungeon.player.getCardColor() != ZixiuEnums.ZIXIU_COLOR) {
            return;
        }
        if (!tried) {
            tried = true;
            bg = ImageMaster.loadImage(BG);
        }
        if (bg == null) {
            return;
        }
        sb.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        sb.draw(bg, 0.0F, 0.0F, Settings.WIDTH, Settings.HEIGHT);
    }
}
