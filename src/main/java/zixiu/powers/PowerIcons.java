package zixiu.powers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.util.HashMap;
import java.util.Map;

/**
 * 字修自己的能力图标，替换之前借用的原版图标（poison / flex / dexterity…）。
 *
 * AbstractPower.loadRegion 只能在原版 powers.atlas 里找图，mod 加不了新区域，
 * 所以这里自己造 AtlasRegion：整张图取样，但把 packedWidth 设成想要的绘制尺寸。
 * 贴图由 tools/make-power-icons.py 生成（96×96，按 48 绘制，留了 2 倍余量）。
 */
public final class PowerIcons {

    private static final int SMALL = 48;
    private static final int LARGE = 96;
    private static final String DIR = "zixiu/images/powers/";

    private static final Map<String, Texture> CACHE = new HashMap<String, Texture>();

    private PowerIcons() {
    }

    public static void apply(AbstractPower power, String key) {
        Texture texture = texture(key);
        if (texture == null) {
            return;
        }
        power.region48 = region(texture, SMALL);
        power.region128 = region(texture, LARGE);
    }

    private static Texture texture(String key) {
        Texture cached = CACHE.get(key);
        if (cached != null) {
            return cached;
        }
        Texture loaded = ImageMaster.loadImage(DIR + key + ".png");
        if (loaded != null) {
            CACHE.put(key, loaded);
        }
        return loaded;
    }

    private static TextureAtlas.AtlasRegion region(Texture texture, int size) {
        TextureAtlas.AtlasRegion region =
                new TextureAtlas.AtlasRegion(texture, 0, 0, texture.getWidth(), texture.getHeight());
        region.packedWidth = size;
        region.packedHeight = size;
        region.originalWidth = size;
        region.originalHeight = size;
        return region;
    }
}
