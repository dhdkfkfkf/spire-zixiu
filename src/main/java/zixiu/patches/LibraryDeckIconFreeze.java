package zixiu.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.ui.panels.TopPanel;

import java.lang.reflect.Field;

/**
 * 图鉴开着时，别让**原版牌组图标**跟着晃。
 *
 * <p>原版 {@code updateDeckViewButtonLogic()} 里有一句「只要 {@code screen == MASTER_DECK_VIEW}
 * 就按 sin 一直摇」——图鉴借用的正是这个屏幕格子，所以牌组图标会以为牌组界面开着，跟着一起晃。
 * 图鉴自己那块字典图标的晃动在 {@code ZixiuLibrary} 里单独做。
 *
 * <p>这里不跳过整个方法（否则牌组按钮的 hover / 悬浮说明也会失效），只在方法跑完后把
 * {@code deckAngle} 归零，图标就不动了；点击 / 切换仍由 {@code LibraryTopBarSwitch} + 原版逻辑处理。
 */
@SpirePatch(clz = TopPanel.class, method = "updateDeckViewButtonLogic")
public class LibraryDeckIconFreeze {

    private static Field deckAngleField;

    @SpirePostfixPatch
    public static void Postfix(TopPanel __instance) {
        if (!zixiu.ui.ZixiuLibrary.isOpen()) {
            return;
        }
        Field field = deckAngleField;
        if (field == null) {
            try {
                field = TopPanel.class.getDeclaredField("deckAngle");
                field.setAccessible(true);
                deckAngleField = field;
            } catch (Exception e) {
                return;
            }
        }
        try {
            field.setFloat(__instance, 0.0F);
        } catch (Exception ignored) {
            // 拿不到就保持原样，最多是牌组图标跟着晃一下，不影响功能。
        }
    }
}
