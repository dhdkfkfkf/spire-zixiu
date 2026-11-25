package zixiu.ui;

import basemod.interfaces.PostRenderSubscriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import zixiu.character.ZixiuCharacter;
import zixiu.core.ZixiuConfig;

/**
 * 选角界面的「拆合规则」选择器。
 *
 * 两行、都居中在屏幕水平中线附近，避免像之前那样贴着「启程」按钮往右溢出：
 * <pre>
 *        拆合规则 · 方案一        ← 小号金字
 *    ＜产出虚无，材料入弃牌堆＞    ← 米色，整行可点
 * </pre>
 * 点这一行就在两种方案之间切换（只有两项，不用分清左右箭头）。
 * 输入处理放在 render 里：BaseMod 的 PostUpdate 钩子在主菜单不跑。
 */
public class CharSelectRulesButton implements PostRenderSubscriber {

    /** 标题相对当前值那一行的上移量。 */
    private static final float LABEL_Y_OFFSET = 30.0F;
    /** 锚点：屏幕水平中线、距底部的高度比例。 */
    private static final float ANCHOR_X_RATIO = 0.5F;
    private static final float ANCHOR_Y_RATIO = 0.20F;
    /** 整行可点区域的额外余量（上下）。 */
    private static final float CLICK_PAD_Y = 16.0F;

    private static final GlyphLayout LAYOUT = new GlyphLayout();

    /** 自己维护左键按下沿：PostRender 里 justClickedLeft 可能已经被清掉了。 */
    private static boolean wasLeftDown;

    @Override
    public void receivePostRender(SpriteBatch sb) {
        if (!visible()) {
            return;
        }
        float s = Settings.scale;
        float cx = Settings.WIDTH * ANCHOR_X_RATIO;
        float cy = Settings.HEIGHT * ANCHOR_Y_RATIO;

        String title = "拆合规则 · " + (ZixiuConfig.fusionMode() == 1 ? "方案一" : "方案二");
        String value = ZixiuConfig.exhaustsMaterials()
                ? "＜产出保留，材料消耗＞"
                : "＜产出虚无，材料入弃牌堆＞";

        // 整行（含两侧尖括号）作为点击区
        LAYOUT.setText(FontHelper.tipBodyFont, value);
        float halfW = LAYOUT.width / 2.0F + 8.0F * s;
        float halfH = LAYOUT.height / 2.0F + CLICK_PAD_Y * s;
        boolean hovering = Math.abs(InputHelperX() - cx) <= halfW
                && Math.abs(InputHelperY() - cy) <= halfH;

        boolean down = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
        boolean pressedNow = (down && !wasLeftDown) || com.megacrit.cardcrawl.helpers.input.InputHelper.justClickedLeft;
        wasLeftDown = down;
        if (hovering && pressedNow) {
            ZixiuConfig.toggleFusionMode();
            ScreenToast.show("拆合规则：" + ZixiuConfig.fusionModeLabel());
            wasLeftDown = true;
            // 切换后字符串变了，重新算一次标题/数值
            title = "拆合规则 · " + (ZixiuConfig.fusionMode() == 1 ? "方案一" : "方案二");
            value = ZixiuConfig.exhaustsMaterials()
                    ? "＜产出保留，材料消耗＞"
                    : "＜产出虚无，材料入弃牌堆＞";
        }

        FontHelper.renderFontCentered(sb, FontHelper.charDescFont, title,
                cx, cy + LABEL_Y_OFFSET * s,
                hovering ? Settings.GOLD_COLOR : new Color(0.79F, 0.64F, 0.36F, 0.9F));
        FontHelper.renderFontCentered(sb, FontHelper.tipBodyFont, value,
                cx, cy, hovering ? Settings.GOLD_COLOR : Settings.CREAM_COLOR);
    }

    private static float InputHelperX() {
        return com.megacrit.cardcrawl.helpers.input.InputHelper.mX;
    }

    private static float InputHelperY() {
        return com.megacrit.cardcrawl.helpers.input.InputHelper.mY;
    }

    private static CharacterSelectScreen screen() {
        return com.megacrit.cardcrawl.core.CardCrawlGame.mainMenuScreen == null
                ? null
                : com.megacrit.cardcrawl.core.CardCrawlGame.mainMenuScreen.charSelectScreen;
    }

    /** 只在选角界面、且当前选中的是砚秋时显示。 */
    private static boolean visible() {
        // 进局后 mainMenuScreen.screen 仍停留在 CHAR_SELECT，必须用 CardCrawlGame.mode 卡死
        if (com.megacrit.cardcrawl.core.CardCrawlGame.mode
                != com.megacrit.cardcrawl.core.CardCrawlGame.GameMode.CHAR_SELECT) {
            return false;
        }
        if (com.megacrit.cardcrawl.core.CardCrawlGame.mainMenuScreen == null
                || com.megacrit.cardcrawl.core.CardCrawlGame.mainMenuScreen.screen
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
