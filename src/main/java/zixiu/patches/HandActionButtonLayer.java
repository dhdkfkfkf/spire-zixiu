package zixiu.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import zixiu.ui.HandActionButton;

/**
 * 把「合 / 拆」按钮画在 {@code AbstractDungeon.render} 之后。
 *
 * 关键是**不能**再挂在 BaseMod 的 PostRender 上：那个钩子在整个 frame 的最后，
 * 而鼠标光标（{@code CardCrawlGame.cursor.render}）是在它之前画的——所以按钮会压住指针，
 * 点下去看着像鼠标钻到字底下了。这里退到光标之前一层，指针就正常盖在按钮上面。
 */
@SpirePatch(clz = AbstractDungeon.class, method = "render",
        paramtypes = {"com.badlogic.gdx.graphics.g2d.SpriteBatch"})
public class HandActionButtonLayer {

    @SpirePostfixPatch
    public static void Postfix(AbstractDungeon __instance, SpriteBatch sb) {
        HandActionButton.renderAll(sb);
        // 图鉴卡墙在 LibraryPageLayer（顶栏之前）画；这里只画最上层的字典图标
        zixiu.ui.ZixiuLibrary.renderIcon(sb);
    }
}
