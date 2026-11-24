package zixiu.ui;

import basemod.interfaces.PostRenderSubscriber;
import basemod.interfaces.PostUpdateSubscriber;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * 手牌区左侧的方按钮。合、拆共用同一套绘制与点击逻辑。
 *
 * 两个 StS 自定义 UI 的坑都在这里处理：
 * 1. Hitbox 尺寸必须在每帧重算——模块加载时 Settings.scale 还是 0。
 * 2. Hitbox.update() 只把 clickStarted 转成 clicked，从不自己设置 clickStarted。
 */
public class HandActionButton implements PostUpdateSubscriber, PostRenderSubscriber {

    private static final Logger logger = LogManager.getLogger(HandActionButton.class);
    private static final float SIZE = 90.0F;
    private static final float OFFSET_X = 40.0F;

    private final String label;

    private static Texture glow;
    private final float offsetY;
    private final BooleanSupplier visible;
    private final BooleanSupplier enabled;
    private final Runnable action;
    private final Supplier<String> disabledToast;
    private final Hitbox hb = new Hitbox(SIZE, SIZE);
    private int frames = 0;

    public HandActionButton(String label, float offsetY,
                            BooleanSupplier visible, BooleanSupplier enabled,
                            Runnable action, Supplier<String> disabledToast) {
        this.label = label;
        this.offsetY = offsetY;
        this.visible = visible;
        this.enabled = enabled;
        this.action = action;
        this.disabledToast = disabledToast;
    }

    @Override
    public void receivePostUpdate() {
        this.frames++;
        if (!this.visible.getAsBoolean()) {
            hb.hovered = false;
            if (this.frames % 180 == 0) {
                logger.info("[zixiu] btn " + label + " hidden");
            }
            return;
        }

        hb.width = SIZE * Settings.scale;
        hb.height = SIZE * Settings.scale;
        hb.move(OFFSET_X * Settings.scale + SIZE * Settings.scale / 2.0F,
                offsetY * Settings.scale + SIZE * Settings.scale / 2.0F);

        hb.update();
        if (this.frames % 60 == 0) {
            logger.info("[zixiu] btn " + label + " on=" + this.enabled.getAsBoolean()
                    + " hovered=" + hb.hovered + " mouse=(" + InputHelper.mX + "," + InputHelper.mY + ")"
                    + " hb=(" + hb.cX + "," + hb.cY + ") size=(" + hb.width + "," + hb.height + ")"
                    + " hand=[" + HandActions.handSummary() + "]");
        }
        if (hb.hovered && InputHelper.justClickedLeft) {
            hb.clickStarted = true;
        }
        if (hb.clicked) {
            hb.clicked = false;
            logger.info("[zixiu] btn " + label + " clicked, enabled=" + this.enabled.getAsBoolean());
            if (this.enabled.getAsBoolean()) {
                this.action.run();
            } else if (this.disabledToast != null) {
                String message = this.disabledToast.get();
                if (message != null) {
                    ScreenToast.show(message);
                }
            }
        }
    }

    @Override
    public void receivePostRender(SpriteBatch sb) {
        if (!this.visible.getAsBoolean()) {
            return;
        }
        boolean on = this.enabled.getAsBoolean();
        float x = OFFSET_X * Settings.scale;
        float y = offsetY * Settings.scale;
        float w = SIZE * Settings.scale;
        float h = SIZE * Settings.scale;

        // 不画底板：只留文字，避免挡住鼠标指针
        Color color;
        if (!on) {
            color = new Color(0.55F, 0.55F, 0.55F, 1.0F);
        } else if (hb.hovered) {
            color = new Color(1.0F, 0.90F, 0.55F, 1.0F);
        } else {
            color = Color.WHITE;
        }
        // 文字先描一层暗影，浅色背景上也读得清
        sb.setColor(new Color(0.0F, 0.0F, 0.0F, 0.55F));
        FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, label,
                x + w / 2.0F, y + h / 2.0F - 2.0F * Settings.scale,
                new Color(0.0F, 0.0F, 0.0F, 0.55F), 1.3F);
        FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, label,
                x + w / 2.0F, y + h / 2.0F, color, 1.3F);

        // 悬停时在文字后面点一盏金色光晕，代替原来的下划线
        if (on && hb.hovered) {
            if (glow == null) {
                glow = ImageMaster.loadImage("zixiu/images/cardui/btn_glow.png");
            }
            if (glow != null) {
                sb.setColor(new Color(1.0F, 1.0F, 1.0F, 0.55F));
                sb.draw(glow, x + w * 0.5F - w * 0.9F, y + h * 0.5F - w * 0.9F,
                        w * 1.8F, w * 1.8F);
            }
        }
        sb.setColor(Color.WHITE);
    }
}
