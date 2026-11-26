package zixiu.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;

/**
 * 图鉴开着的时候，屏蔽地图的输入。
 *
 * 地图的节点 hover/点击是在 {@code DungeonMapScreen.update()} 里处理的，
 * 它不看 {@code AbstractDungeon.isScreenUp}，所以图鉴盖在上面时鼠标滑过去
 * 还会触发地图节点的光圈和点击——这里是直接不跑它的 update。
 */
@SpirePatch(clz = DungeonMapScreen.class, method = "update")
public class MapInputBlock {

    @SpirePrefixPatch
    public static SpireReturn<Void> Prefix(DungeonMapScreen __instance) {
        if (zixiu.ui.ZixiuLibrary.isOpen()) {
            return SpireReturn.Return(null);
        }
        return SpireReturn.Continue();
    }
}
