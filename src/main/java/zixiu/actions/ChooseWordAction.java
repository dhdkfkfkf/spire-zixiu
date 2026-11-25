package zixiu.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDiscardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zixiu.cards.CardComponents;
import zixiu.core.FusionTracker;
import zixiu.lexicon.WordSpec;

import java.util.ArrayList;
import java.util.List;

/**
 * 选字面板：一组部件对应多个字时，让玩家挑一个成型。
 * 面板复用 CardRewardScreen.customCombatOpen。
 */
public class ChooseWordAction extends AbstractGameAction {

    private static final Logger logger = LogManager.getLogger(ChooseWordAction.class);

    private final List<WordSpec> options;
    /** 这组合成的结果是否是升级版（升级材料数 >= 未升级材料数）。 */
    private final boolean upgradeResult;
    private boolean opened = false;

    public ChooseWordAction(List<WordSpec> options) {
        this(options, false);
    }

    public ChooseWordAction(List<WordSpec> options, boolean upgradeResult) {
        this.options = options;
        this.upgradeResult = upgradeResult;
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_FAST;
    }

    @Override
    public void update() {
        if (!opened) {
            ArrayList<AbstractCard> cards = new ArrayList<AbstractCard>();
            for (WordSpec spec : options) {
                AbstractCard card = CardComponents.create(spec.glyph());
                if (card != null) {
                    if (upgradeResult && card.canUpgrade()) {
                        card.upgrade();
                    }
                    cards.add(card);
                }
            }
            AbstractDungeon.cardRewardScreen.customCombatOpen(cards, "选择要写的字", false);
            this.opened = true;
            return;
        }

        if (AbstractDungeon.cardRewardScreen.discoveryCard != null) {
            AbstractCard picked = AbstractDungeon.cardRewardScreen.discoveryCard;
            AbstractDungeon.cardRewardScreen.discoveryCard = null;
            putIntoHand(picked);
            this.isDone = true;
        }
    }

    public static void putIntoHand(AbstractCard card) {
        zixiu.ui.DeferredCardInsert.enqueue(card);
    }

    public static void putIntoHandNow(AbstractCard card) {
        AbstractCard copy = card.makeStatEquivalentCopy();
        copy.current_x = -1000.0F * Settings.xScale;
        // 墨迹未干与方案无关：两种方案下合出来的牌当回合都不能拆
        FusionTracker.markFresh(copy);
        // 只有「虚无」随方案变化：方案 1 产出虚无，方案 2 保留
        if (zixiu.core.ZixiuConfig.etherealResults()) {
            zixiu.cards.CardComponents.markEthereal(copy);
        }
        logger.info("[zixiu] fusion result " + copy.cardID
                + " fresh=true ethereal=" + copy.isEthereal
                + " mode=" + zixiu.core.ZixiuConfig.fusionMode());
        // 直接插入：手机版在满手时会把 ShowCardAndAddToHandEffect 的牌转进弃牌堆
        zixiu.cards.CardComponents.insertIntoHand(copy);
    }
}
