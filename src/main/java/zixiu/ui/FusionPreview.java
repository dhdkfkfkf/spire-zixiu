package zixiu.ui;

import basemod.interfaces.PostRenderSubscriber;
import basemod.interfaces.PostUpdateSubscriber;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import zixiu.cards.CardComponents;
import zixiu.lexicon.Component;
import zixiu.lexicon.WordSpec;
import zixiu.lexicon.ZixiuLexicon;

import java.util.ArrayList;
import java.util.List;

/**
 * 合字预知：合字选牌界面里，放进两张以上材料就实时列出「能写出什么字、各有什么效果」。
 *
 * 画在右侧那块空白区域（{@link PreviewPanel}），和拆字预知同一套版式。
 */
public class FusionPreview implements PostUpdateSubscriber, PostRenderSubscriber {

    private static final int MAX_OPTIONS = 6;

    @Override
    public void receivePostUpdate() {
        // 标志清理统一交给 HandActions（选牌界面是 action 队列下一帧才弹出来的）
        HandActions.tickSelectScreen();
    }

    @Override
    public void receivePostRender(SpriteBatch sb) {
        if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.HAND_SELECT
                || !HandActions.fusing
                || AbstractDungeon.handCardSelectScreen == null) {
            return;
        }
        List<AbstractCard> selected = AbstractDungeon.handCardSelectScreen.selectedCards.group;
        if (selected.isEmpty()) {
            return;
        }
        if (selected.size() < 2) {
            PreviewPanel.draw(sb, "合字", single("再放一张材料就能看出能写什么"));
            return;
        }

        boolean upgradeResult = HandActions.shouldUpgradeResult(selected);
        List<Component> parts = CardComponents.expandAll(selected);
        List<WordSpec> options = new ArrayList<WordSpec>();
        for (WordSpec spec : ZixiuLexicon.optionsFor(parts)) {
            if (CardComponents.isImplemented(spec.glyph())) {
                options.add(spec);
            }
        }
        if (options.isEmpty()) {
            PreviewPanel.draw(sb, "合字", single("这几张牌合不出结果"));
            return;
        }

        String title = options.size() == 1 ? "将写出" : "可能写出（确定后选一个）";
        List<String> items = new ArrayList<String>();
        for (int i = 0; i < options.size() && i < MAX_OPTIONS; i++) {
            AbstractCard preview = CardComponents.create(options.get(i).glyph());
            if (preview == null) {
                continue;
            }
            if (upgradeResult) {
                HandActions.upgradeIfPossible(preview);
            }
            items.add(preview.name + "：" + PreviewPanel.describe(preview));
        }
        PreviewPanel.draw(sb, title, items);
    }

    private static List<String> single(String line) {
        List<String> list = new ArrayList<String>();
        list.add(line);
        return list;
    }
}
