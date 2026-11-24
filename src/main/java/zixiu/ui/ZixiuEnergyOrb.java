package zixiu.ui;

import basemod.abstracts.CustomEnergyOrb;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;

/**
 * 字修的战斗能量球（左下角那团「能量」）。
 *
 * BaseMod 的 CustomEnergyOrb 会按 [有能量层…, 底层, 没能量层…] 的顺序逐层旋转绘制，
 * 这里再在中心叠一层原版篝火火焰帧，让余烬有跳动感。数字（当前/上限）由原版
 * EnergyPanel 在最上层绘制，能量球只负责底下的动画。
 *
 * 贴图由 tools/make-energy-orb.py 生成。
 */
public class ZixiuEnergyOrb extends CustomEnergyOrb {

    private static final String DIR = "zixiu/images/ui/energy/";

    /**
     * 5 层有能量 + 1 层铁环底 + 5 层没能量。
     * 必须是 5 层：BaseMod 的 updateOrb 写死了 angles[0..4]，层数少了会越界崩溃。
     */
    private static final String[] ORB_TEXTURES = {
            DIR + "layer1.png", DIR + "layer2.png", DIR + "layer3.png",
            DIR + "layer4.png", DIR + "layer5.png",
            DIR + "base.png",
            DIR + "layer1d.png", DIR + "layer2d.png", DIR + "layer3d.png",
            DIR + "layer4d.png", DIR + "layer5d.png",
    };

    /** 每层的旋转速度（度/秒），正负交错，转起来才不会糊成一团。 */
    private static final float[] LAYER_SPEEDS = {-26.0F, 18.0F, -38.0F, 34.0F, -54.0F};

    private static final Texture[] FLAME_FRAMES = {
            ImageMaster.FLAME_ANIM_1, ImageMaster.FLAME_ANIM_2, ImageMaster.FLAME_ANIM_3,
            ImageMaster.FLAME_ANIM_4, ImageMaster.FLAME_ANIM_5, ImageMaster.FLAME_ANIM_6,
    };
    private static final float FRAME_TIME = 0.08F;
    private static float clock;

    public ZixiuEnergyOrb() {
        super(ORB_TEXTURES, DIR + "vfx.png", LAYER_SPEEDS);
    }

    @Override
    public void renderOrb(SpriteBatch sb, boolean enabled, float x, float y) {
        super.renderOrb(sb, enabled, x, y);
        if (!enabled) {
            return;
        }
        // 有能量时，中心叠一层加法混合的火焰，透明度呼吸
        clock += Gdx.graphics.getDeltaTime();
        Texture flame = FLAME_FRAMES[(int) (clock / FRAME_TIME) % FLAME_FRAMES.length];
        if (flame == null) {
            return;
        }
        float pulse = 0.26F + 0.10F * (float) Math.sin(clock * 4.6F);
        float size = 84.0F * Settings.scale;
        sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        sb.setColor(1.0F, 0.72F, 0.30F, pulse);
        sb.draw(flame, x - size / 2.0F, y - size / 2.0F, size, size);
        sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sb.setColor(Color.WHITE);
    }
}
