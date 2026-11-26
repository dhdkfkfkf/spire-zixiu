package zixiu.ui;

import basemod.interfaces.PostRenderSubscriber;
import basemod.interfaces.PostUpdateSubscriber;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import zixiu.cards.CardComponents;
import zixiu.lexicon.Component;
import zixiu.lexicon.ZixiuLexicon;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 拆字预知：拆解选牌界面里，把要拆的牌**放进去之后**，右侧面板列出会拆出哪些部件、各自什么效果。
 *
 * 和合字预知同一个逻辑（看 selectedCards，不看 hover）：
 * 悬停只是看牌，不能当「选了这张」用。
 */
public class SplitPreview implements PostUpdateSubscriber, PostRenderSubscriber {

    @Override
    public void receivePostUpdate() {
        // 标志清理统一交给 HandActions：选牌界面是 action 队列下一帧才弹出来的，
        // 这里直接按 screen 清，会把刚点出来的状态立刻抹掉。
        HandActions.tickSelectScreen();
    }

    @Override
    public void receivePostRender(SpriteBatch sb) {
        if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.HAND_SELECT) {
            return;
        }
        if (!HandActions.splitting) {
            return;
        }
        if (AbstractDungeon.handCardSelectScreen == null) {
            return;
        }
        List<AbstractCard> selected = AbstractDungeon.handCardSelectScreen.selectedCards.group;
        if (selected.isEmpty()) {
            List<String> hint = new ArrayList<String>();
            hint.add("把一张成品牌放进上面，就能看出会拆成什么");
            PreviewPanel.draw(sb, "拆字", hint);
            return;
        }

        AbstractCard card = selected.get(0);
        String glyph = CardComponents.glyphOf(card);
        if (glyph == null) {
            return;
        }
        List<Component> parts = ZixiuLexicon.partsOf(glyph);
        if (parts.isEmpty()) {
            return;
        }

        // 重复部件合并成「木 ×3」，每条带部件牌的实际效果（按源牌是否升级）
        Map<Component, Integer> counts = new LinkedHashMap<Component, Integer>();
        for (Component part : parts) {
            counts.put(part, counts.containsKey(part) ? counts.get(part) + 1 : 1);
        }
        List<String> items = new ArrayList<String>();
        for (Map.Entry<Component, Integer> entry : counts.entrySet()) {
            AbstractCard piece = CardComponents.createComponent(entry.getKey());
            if (card.upgraded && piece != null) {
                HandActions.upgradeIfPossible(piece);
            }
            String name = entry.getKey().glyph() + (entry.getValue() > 1 ? " ×" + entry.getValue() : "");
            items.add(name + "：" + PreviewPanel.describe(piece));
        }
        PreviewPanel.draw(sb, "拆「" + glyph + "」", items);
    }
}
