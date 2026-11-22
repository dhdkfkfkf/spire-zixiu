package zixiu.events;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import zixiu.ZixiuMod;
import zixiu.cards.CardComponents;
import zixiu.lexicon.Component;

/**
 * 错字：碑上刻着一个写错的字。
 */
public class CuoZi extends AbstractImageEvent {

    public static final String ID = ZixiuMod.makeID("CuoZi");
    private static final EventStrings STRINGS = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] FALLBACK = {
            "碑上刻着一个写错的字。笔画的顺序乱了。",
            "拆开它：获得 2 张随机部件牌",
            "照抄：获得 1 张随机成品牌，失去 5 点生命",
            "离开",
            "你把碑上的字一笔笔拆下来。",
            "你照着错字抄了一遍，手指被划破了。",
            "你没碰那块碑。",
            "[离开]"
    };
    private static final String[] D = STRINGS != null && STRINGS.DESCRIPTIONS != null
            && STRINGS.DESCRIPTIONS.length >= 8 ? STRINGS.DESCRIPTIONS : FALLBACK;

    private static final Component[] POOL = {Component.REN, Component.KOU, Component.MU, Component.HUO};
    private static final String[] WORDS = {"呆", "杏", "困", "束"};

    private int screen = 0;

    public CuoZi() {
        super("错字", D[0], "images/events/cuozi.png");
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
            for (int i = 0; i < 2; i++) {
                Component component = POOL[AbstractDungeon.cardRandomRng.random(POOL.length - 1)];
                AbstractCard card = CardComponents.createComponent(component);
                if (card != null) {
                    AbstractDungeon.player.masterDeck.addToTop(card);
                }
            }
            result = D[4];
        } else if (buttonPressed == 1) {
            AbstractCard card = CardComponents.create(WORDS[AbstractDungeon.cardRandomRng.random(WORDS.length - 1)]);
            if (card != null) {
                AbstractDungeon.player.masterDeck.addToTop(card);
            }
            AbstractDungeon.player.damage(new DamageInfo(null, 5, DamageInfo.DamageType.HP_LOSS));
            result = D[5];
        } else {
            result = D[6];
        }

        this.imageEventText.updateBodyText(result);
        this.imageEventText.updateDialogOption(0, D[7]);
        this.imageEventText.clearRemainingOptions();
    }
}
