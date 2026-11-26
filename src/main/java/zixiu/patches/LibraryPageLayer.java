package zixiu.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.ui.panels.TopPanel;

/**
 * 图鉴的卡墙 / 排序条画在**顶栏之前**。
 *
 * <p>原版看牌界面也是这个层次：卡牌先画，然后才是顶栏（图标 + 悬浮说明）。这样图鉴开着时，
 * 鼠标划过地图 / 牌组 / 设置按钮，那些原版说明会正常盖在卡墙上，而不是被卡墙压住。
 * 顶栏最右边的字典图标本身仍在 {@code HandActionButtonLayer} 里画（永远最上层）。
 */
@SpirePatch(clz = TopPanel.class, method = "render",
        paramtypes = {"com.badlogic.gdx.graphics.g2d.SpriteBatch"})
public class LibraryPageLayer {

    @SpirePrefixPatch
    public static void Prefix(TopPanel __instance, SpriteBatch sb) {
        zixiu.ui.ZixiuLibrary.renderPage(sb);
    }
}
