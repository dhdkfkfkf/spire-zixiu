package zixiu.events;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import zixiu.ZixiuMod;
import zixiu.cards.CardComponents;
import zixiu.lexicon.Component;

/**
 * 残卷：一册被烧掉一半的书。
 */
public class CanJuan extends AbstractImageEvent {

    public static final String ID = ZixiuMod.makeID("CanJuan");
    private static final EventStrings STRINGS = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] FALLBACK = {
            "一册被烧掉一半的书，剩下的字还在发热。",
            "抄录：获得 1 张随机稀有牌，最大生命 -3",
            "修补：获得 2 张随机部件牌",
            "烧掉：获得 1 瓶朱砂",
            "你抄下残页，字迹烫得手指发麻。",
            "你把碎片重新装订，书脊还是断的。",
            "你把残卷扔进火里，火苗变成了朱红色。",
            "[离开]"
    };
    private static final String[] D = STRINGS != null && STRINGS.DESCRIPTIONS != null
            && STRINGS.DESCRIPTIONS.length >= 8 ? STRINGS.DESCRIPTIONS : FALLBACK;


    private int screen = 0;

    public CanJuan() {
        super("残卷", D[0], "images/events/canjuan.png");
        this.imageEventText.setDialogOption(D[1]);
        this.imageEventText.setDialogOption(D[2]);
        this.imageEventText.setDialogOption(D[3]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        if (this.screen != 0) {
            openMap();
            return;
        }
        this.screen = 1;

        String result;
        if (buttonPressed == 0) {
            java.util.List<String> pool = CardComponents.glyphsOfRarity(
                    AbstractCard.CardRarity.RARE);
            if (pool.isEmpty()) {
                pool = CardComponents.implementedGlyphs();
            }
            AbstractCard card = CardComponents.create(
                    pool.get(AbstractDungeon.cardRandomRng.random(pool.size() - 1)));
            if (card != null) {
                AbstractDungeon.player.masterDeck.addToTop(card);
            }
            AbstractDungeon.player.decreaseMaxHealth(3);
            result = D[4];
        } else if (buttonPressed == 1) {
            for (int i = 0; i < 2; i++) {
                Component component = Component.values()[AbstractDungeon.cardRandomRng.random(Component.values().length - 1)];
                AbstractCard card = CardComponents.createComponent(component);
                if (card != null) {
                    AbstractDungeon.player.masterDeck.addToTop(card);
                }
            }
            result = D[5];
        } else {
            AbstractPotion potion = new zixiu.potions.Cinnabar();
            AbstractDungeon.getCurrRoom().addPotionToRewards(potion);
            result = D[6];
        }

        this.imageEventText.updateBodyText(result);
        this.imageEventText.updateDialogOption(0, D[7]);
        this.imageEventText.clearRemainingOptions();
    }
}
