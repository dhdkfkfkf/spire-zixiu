package zixiu.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.ui.panels.TopPanel;

/**
 * 图鉴开着时，点顶栏的地图 / 牌组 / 设置可以直接切过去。
 *
 * <p>不带这个补丁的话，图鉴占着 {@code MASTER_DECK_VIEW}，牌组按钮会被原版当成「再点一次关闭」，
 * 地图 / 设置也会按「从牌组界面切走」的路径处理。这里先在图鉴这一帧的点击里把它收起来
 * （走原版 {@code closeCurrentScreen}，界面、遮罩、手牌都还原），再让原版照常处理这次点击——
 * 于是三个按钮的行为和「没开图鉴时」完全一样，自然就互相切换了。
 */
@SpirePatch(clz = TopPanel.class, method = "update")
public class LibraryTopBarSwitch {

    @SpirePrefixPatch
    public static void Prefix(TopPanel __instance) {
        if (!zixiu.ui.ZixiuLibrary.isOpen() || !InputHelper.justClickedLeft) {
            return;
        }
        if (inHb(__instance.mapHb) || inHb(__instance.deckHb) || inHb(__instance.settingsHb)) {
            zixiu.ui.ZixiuLibrary.closeForTopBarSwitch();
        }
    }

    private static boolean inHb(Hitbox hb) {
        return hb != null
                && InputHelper.mX >= hb.x && InputHelper.mX <= hb.x + hb.width
                && InputHelper.mY >= hb.y && InputHelper.mY <= hb.y + hb.height;
    }
}
