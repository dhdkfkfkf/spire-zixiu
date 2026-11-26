package zixiu.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.screens.MasterDeckViewScreen;

/**
 * 图鉴占着 {@code MASTER_DECK_VIEW} 这个屏幕格子，但这里要的是我们自己的卡墙，
 * 所以原版牌组界面的 update 必须让开（否则它会处理自己的滚动 / 点击 / Esc）。
 */
@SpirePatch(clz = MasterDeckViewScreen.class, method = "update")
public class LibraryDeckViewUpdateBlock {

    @SpirePrefixPatch
    public static SpireReturn<Void> Prefix() {
        if (zixiu.ui.ZixiuLibrary.isOpen()) {
            return SpireReturn.Return(null);
        }
        return SpireReturn.Continue();
    }
}
