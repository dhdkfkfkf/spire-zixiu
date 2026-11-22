package zixiu.ui;

import basemod.interfaces.PostRenderSubscriber;
import basemod.interfaces.PostUpdateSubscriber;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import zixiu.cards.CardComponents;
import zixiu.lexicon.Component;
import zixiu.lexicon.ZixiuLexicon;

import java.util.List;

/**
 * 拆字预览：在拆解选牌界面里，悬停成品牌时在上方显示会拆出哪些部件。
 */
public class SplitPreview implements PostUpdateSubscriber, PostRenderSubscriber {

    @Override
    public void receivePostUpdate() {
        if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.HAND_SELECT) {
            HandActions.splitting = false;
        }
    }

    @Override
    public void receivePostRender(SpriteBatch sb) {
        if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.HAND_SELECT) {
            return;
        }
        if (!HandActions.splitting) {
            return;
        }
        AbstractCard hovered = AbstractDungeon.handCardSelectScreen.hoveredCard;
        if (hovered == null) {
            return;
        }
        String glyph = CardComponents.glyphOf(hovered);
        if (glyph == null) {
            return;
        }
        List<Component> parts = ZixiuLexicon.partsOf(glyph);
        if (parts.isEmpty()) {
            return;
        }
        StringBuilder text = new StringBuilder("拆「").append(glyph).append("」→ ");
        for (int i = 0; i < parts.size(); i++) {
            if (i > 0) {
                text.append(" + ");
            }
            text.append(parts.get(i).glyph());
        }
        FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, text.toString(),
                hovered.current_x, hovered.current_y + hovered.hb.height * 0.72F, Color.WHITE);
    }
}
