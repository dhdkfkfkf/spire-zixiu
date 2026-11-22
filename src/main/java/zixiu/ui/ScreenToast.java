package zixiu.ui;

import basemod.interfaces.PostRenderSubscriber;
import basemod.interfaces.PostUpdateSubscriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;

/**
 * 屏幕中央的白字提示，1.8 秒后淡出。
 */
public class ScreenToast implements PostUpdateSubscriber, PostRenderSubscriber {

    private static final float DURATION = 1.8F;
    private static String message;
    private static float timer;

    public static void show(String text) {
        message = text;
        timer = DURATION;
    }

    @Override
    public void receivePostUpdate() {
        if (timer > 0.0F) {
            timer -= Gdx.graphics.getDeltaTime();
        }
    }

    @Override
    public void receivePostRender(SpriteBatch sb) {
        if (timer <= 0.0F || message == null) {
            return;
        }
        float alpha = Math.min(1.0F, timer / 0.4F);
        FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, message,
                Settings.WIDTH / 2.0F, Settings.HEIGHT * 0.6F, new Color(1.0F, 1.0F, 1.0F, alpha));
    }
}
