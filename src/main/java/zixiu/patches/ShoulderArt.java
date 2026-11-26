package zixiu.patches;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.RestRoom;
import zixiu.character.ZixiuCharacter;

/**
 * 肩像（结算 / 篝火那一层的大图）按场景选图。
 *
 * 原版只有一个 `shoulderImg` 会同时用在「死亡、胜利结算、篝火」三处，
 * `shoulder2Img` 只在篝火选项按下、UI 隐藏后才出现——等于没地方放两张不同的画。
 * 这里拦在 `renderShoulderImg` 上自己画：
 *
 * <ul>
 *   <li>篝火/休息（RestRoom）→ `shoulder2.png`（坐在火边的那张）</li>
 *   <li>其余（死亡 / 胜利结算 / 战斗等）→ `shoulder.png`（伏案收笔的那张）</li>
 * </ul>
 */
@SpirePatch(clz = AbstractPlayer.class, method = "renderShoulderImg",
        paramtypes = {"com.badlogic.gdx.graphics.g2d.SpriteBatch"})
public class ShoulderArt {

    private static final String DESK = "zixiu/images/char/shoulder.png";
    private static final String CAMPFIRE = "zixiu/images/char/shoulder2.png";

    private static Texture desk;
    private static Texture campfire;
    private static boolean loaded;

    @SpirePrefixPatch
    public static SpireReturn<Void> Prefix(AbstractPlayer player, SpriteBatch sb) {
        if (!(player instanceof ZixiuCharacter)) {
            return SpireReturn.Continue();
        }
        if (!loaded) {
            loaded = true;
            desk = ImageMaster.loadImage(DESK);
            campfire = ImageMaster.loadImage(CAMPFIRE);
        }
        Texture texture = inRestRoom() ? campfire : desk;
        if (texture == null) {
            // 图没加载出来就交回原版（至少不会空白）
            return SpireReturn.Continue();
        }
        sb.setColor(Color.WHITE);
        // 和原版 renderShoulderImg 同一套尺寸：1920×1136 直接铺满（乘 UI 缩放）
        sb.draw(texture, 0.0F, 0.0F, 1920.0F * Settings.scale, 1136.0F * Settings.scale);
        return SpireReturn.Return(null);
    }

    private static boolean inRestRoom() {
        try {
            AbstractRoom room = AbstractDungeon.getCurrRoom();
            return room instanceof RestRoom;
        } catch (NullPointerException e) {
            // 还没有地图节点（主菜单等）
            return false;
        }
    }
}
