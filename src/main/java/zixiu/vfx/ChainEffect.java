package zixiu.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

/**
 * 连书触发时从角色头顶飘起的计数文字。
 */
public class ChainEffect extends AbstractGameEffect {

    private static final float DURATION = 1.0F;

    private final String text;
    private final float x;
    private float y;

    public ChainEffect(String text, float x, float y) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.duration = DURATION;
        this.startingDuration = DURATION;
        this.color = new Color(1.0F, 0.86F, 0.45F, 1.0F);
        this.scale = 1.6F * Settings.scale;
    }

    @Override
    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();
        this.y += 70.0F * Settings.scale * Gdx.graphics.getDeltaTime();
        if (this.duration < 0.0F) {
            this.isDone = true;
            return;
        }
        this.color.a = Math.min(1.0F, this.duration / (DURATION * 0.5F));
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(this.color);
        FontHelper.renderFontCentered(sb, FontHelper.cardTitleFont, this.text, this.x, this.y,
                this.color, this.scale);
    }

    @Override
    public void dispose() {
    }
}
