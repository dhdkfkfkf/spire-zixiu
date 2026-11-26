package zixiu.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.screens.MasterDeckViewScreen;

/**
 * 同上：{@code AbstractDungeon.render} 的 MASTER_DECK_VIEW 分支会画原版牌组界面，
 * 图鉴开着时不画（否则玩家自己的牌组会跟图鉴的卡墙叠在一起）。
 */
@SpirePatch(clz = MasterDeckViewScreen.class, method = "render",
        paramtypes = {"com.badlogic.gdx.graphics.g2d.SpriteBatch"})
public class LibraryDeckViewRenderBlock {

    @SpirePrefixPatch
    public static SpireReturn<Void> Prefix(MasterDeckViewScreen __instance, SpriteBatch sb) {
        if (zixiu.ui.ZixiuLibrary.isOpen()) {
            return SpireReturn.Return(null);
        }
        return SpireReturn.Continue();
    }
}
