package zixiu.ui;

import basemod.interfaces.PostRenderSubscriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import zixiu.character.ZixiuCharacter;
import zixiu.core.ZixiuConfig;

/**
 * 选角界面的「拆合规则」选择器。
 *
 * 版式照圣主（fuZhouMod 的 FinalBossChoiceSelector）：锚在「启程」按钮上方，
 * 一行金色标题、一行米色当前值，左右各一个 &lt; &gt; 文字箭头（悬停变金），点箭头切换。
 * 只在选中砚秋时出现。
 *
 * 输入处理放在 render 里：BaseMod 的 PostUpdate 钩子在主菜单不跑，只挂 PostUpdate 的话
 * 文字能画出来但点不动。
 */
public class CharSelectRulesButton implements PostRenderSubscriber {

    /** 箭头相对锚点的水平偏移 / 命中框半宽 / 标题相对当前值的上移量。 */
    private static final float ARROW_X_OFFSET = 118.0F;
    private static final float ARROW_HIT_HALF = 32.0F;
    private static final float LABEL_Y_OFFSET = 44.0F;
    private static final float ARROW_TEXT_Y_NUDGE = 4.0F;
    private static final float ANCHOR_Y_FROM_CONFIRM = 190.0F;
    private static final float ANCHOR_X_FROM_CONFIRM = 110.0F;

    /** 自己维护左键按下沿：PostRender 里 justClickedLeft 可能已经被 InputHelper 清掉了。 */
    private static boolean wasLeftDown;

    @Override
    public void receivePostRender(SpriteBatch sb) {
        if (!visible()) {
            return;
        }
        float cx = anchorX();
        float cy = anchorY();
        float s = Settings.scale;
        float half = ARROW_HIT_HALF * s;

        float leftCx = cx - ARROW_X_OFFSET * s;
        float rightCx = cx + ARROW_X_OFFSET * s;
        boolean hoveringLeft = inside(leftCx, cy, half);
        boolean hoveringRight = inside(rightCx, cy, half);

        boolean down = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
        boolean pressedNow = (down && !wasLeftDown) || InputHelper.justClickedLeft;
        wasLeftDown = down;
        if ((hoveringLeft || hoveringRight) && pressedNow) {
            ZixiuConfig.toggleFusionMode();
            ScreenToast.show("拆合规则：" + ZixiuConfig.fusionModeLabel());
            // 同一个按住状态不要重复触发
            wasLeftDown = true;
        }

        // 标题（金）+ 当前值（米色），两行居中
        FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont,
                "拆合规则 · " + (ZixiuConfig.fusionMode() == 1
                        ? "方案 1（产出虚无）" : "方案 2（产出保留）"),
                cx, cy + LABEL_Y_OFFSET * s, Settings.GOLD_COLOR);
        FontHelper.renderFontCentered(sb, FontHelper.tipBodyFont,
                ZixiuConfig.exhaustsMaterials() ? "材料消耗" : "材料入弃牌堆",
                cx, cy, Settings.CREAM_COLOR);

        // 左右箭头：悬停变金
        Color arrowColor = Settings.CREAM_COLOR;
        FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, "<",
                leftCx, cy + ARROW_TEXT_Y_NUDGE * s,
                hoveringLeft ? Settings.GOLD_COLOR : arrowColor);
        FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, ">",
                rightCx, cy + ARROW_TEXT_Y_NUDGE * s,
                hoveringRight ? Settings.GOLD_COLOR : arrowColor);
    }

    private static boolean inside(float x, float y, float half) {
        return Math.abs(InputHelper.mX - x) <= half && Math.abs(InputHelper.mY - y) <= half;
    }

    private static CharacterSelectScreen screen() {
        return CardCrawlGame.mainMenuScreen == null ? null : CardCrawlGame.mainMenuScreen.charSelectScreen;
    }

    /** 锚点：「启程」按钮上方（比圣主的 Boss 选择器再高一点）。 */
    private static float anchorX() {
        CharacterSelectScreen screen = screen();
        if (screen != null && screen.confirmButton != null && screen.confirmButton.hb != null) {
            return screen.confirmButton.hb.cX - ANCHOR_X_FROM_CONFIRM * Settings.scale;
        }
        return Settings.WIDTH * 0.5F;
    }

    private static float anchorY() {
        CharacterSelectScreen screen = screen();
        if (screen != null && screen.confirmButton != null && screen.confirmButton.hb != null) {
            return screen.confirmButton.hb.cY + ANCHOR_Y_FROM_CONFIRM * Settings.scale;
        }
        return Settings.HEIGHT * 0.28F;
    }

    /** 只在选角界面、且当前选中的是砚秋时显示。 */
    private static boolean visible() {
        // 关键：进局后 mainMenuScreen.screen 仍停留在 CHAR_SELECT，
        // 只判它会让选择器在战斗里继续画、继续吃点击。用 CardCrawlGame.mode 卡死。
        if (CardCrawlGame.mode != CardCrawlGame.GameMode.CHAR_SELECT) {
            return false;
        }
        if (CardCrawlGame.mainMenuScreen == null
                || CardCrawlGame.mainMenuScreen.screen
                        != com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen.CurScreen.CHAR_SELECT) {
            return false;
        }
        CharacterSelectScreen screen = screen();
        if (screen == null || screen.options == null) {
            return false;
        }
        for (CharacterOption option : screen.options) {
            if (option != null && option.selected && option.c instanceof ZixiuCharacter) {
                return true;
            }
        }
        return false;
    }
}
